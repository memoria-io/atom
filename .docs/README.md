# Documentation

## 1.0 Core

### 1.1 The `file` package

* The `ResourceFileOps` utility for reading resource based files even inside JAR packaged projects
* The `ConfigFileOps` utility is a for reading configuration files:
    * Allows nesting of files using a marker e.g `include: sub_file.yaml` would replace this line with content
      of `sub_file.yaml`
    * Reading as a system environment variable if not found as environment variable or else the default value if it was
      supplied:
        * `path: ${JAVA_HOME}`
        * `myVar: ${SOME_VAR:-defaultValue}`
    * Reading java system properties `System.setProperty("MY_SYSTEM_PROPERTY", "2000");`

### 1.2 The Id Package

## 2.0 Eventsourcing

### 2.1 Id generation

Initial decision of moving id generation to become repo responsibility instead of service

* Why: I think adapters should be the one generating Ids, since they're the ones talking to database
* It should be the responsibility of a service how an object is persisted, it should only care about business logic
  not implementations of how that object is saved
* same as for controllers should ony be about converting json to DTOs and selecting which service should handle the
  request.

### 2.2 Value Id objects `EventId` and `CommandId`

While working with domain models passing objects around between events and commands e.g when implementing `Decider` I
found many times it's prune to bugs, the case behind the added value of value objects is obvious and languages like
Scala has direct support for such feature, the good thing is that Java is also going to support value objects in the
near future [Project Valhalla value objects](https://openjdk.org/jeps/8277163)

### 2.3 The one-to-one relationship

An event is produced by one command, a saga event produced one command, this choice was made to allow atomicity of
persisting both events and generated sagaCommands, it also allows simplicity of pipelines, although the choice is
arbitrary, it still seems logically sound - for every action there's a reaction.

### 2.3 EventMeta and CommandMeta

Initially we only had EventId which was even a simple string, but with more additions like timestamp and referencing
commandId and stateId etc, it was time to group in as a meta information.

### 2.4 Replication

Both events and commands should both at least be quorum replicated, to make sure they're not missed.

### 2.5 persistence,

* Events should be persisted permanently
* Permanent Commands persistence:
    * Means commands generated from saga events won't be missed and will be ingested everytime,
    * For idempotency this is safe due to two facts:
        * For already ingested commands which produced events, the commandsIds are loaded on startup
        * For newly generated saga commands the sagaSource uninvalidated cache would guard against them
* Ephemeral commands persistence, means that we'll need

### 2.6 The case of Saga regeneration for the already persisted events

Saga is an evolving state machine, and many times we'd need to have a new command created based on certain event.

This means the Saga generated commands should be idempotent.

* In order to do that, `Option<EventId> sagaSource` should be added to the command meta and eventMeta, to explain this
  it's a bit of chicken egg paradox,
    * For an empty pipeline state, the newly handled saga commands would contain `sagaSource`, and the sagaSource
      eventId would be propagated to the generated saga events, when such events are reingested on startup they'd fill
      up the sagaSource hash set bucket, this would allow any newly generated saga command to be checked at `decide()`
      whether it has already produced an event.
* The saga source eventIds hashset should be separate from normal eventId cache, because the idempotency here is for
  commands not events.
    * Deeper thought (can be skipped) it also prevents pipeline from cancelling applying events who were added but not
      actually evolved.
    * This is good candidate for a written test to make sure of such separation
* Another point is that the regeneration of saga commands in the init phase (aka re-ingesting of already persisted
  events) should be under a flag since it generates all saga commands again and it's not ideal (since each event that
  produces saga will be now generating a command again) to do such thing despite it's possible and safe due
  to commands idempotency guaranteed as mentioned previously

### 2.7 The decision of caching of `eventIds`, `SagaSource` and `CommandIds`

* Initially all processed `eventIds` and `commandIds` were kept in hash sets, but I soon came to realise the
  eventId can not be duplicated due to redelivery in a wrong order, meaning the partition could deliver(e3,e2,e1,e1) but
  not (e3,e1,e2,e1) this is guaranteed by any stream framework (e.g kafka nats) that messages in certain partitions are
  saved in same order they were produced, from this I needed to do some sort of runtime compaction to avoid ingesting e1
  twice. Which meant getting rid of the processedEventIds set and only save the previous eventId. Disclaimer I still
  need some more investigation here on the topic.
* Then I decided to use a **cache** for processed CommandIds to save some memory but this now seemed like wrong
  decision, first the cache
  invalidation puts the whole thing at risk, if we use size we're in the risk when we have big partitions and it would
  require manual intervention to increase cache size, and if we use time, what happens to idle partitions, not to
  mention losing a design fact (command X **was** processed)
    * This adds complexity due to the current design that commands are being ephemeral which means, they
      definitely need acknowledgement pattern otherwise they shouldn't be deleted ever, the workaround of using **long**
      keep alive time seems fine, but it still puts a platform that's supposed to be used for highly critical systems at
      risk of losing a saga command even if probability was very low.
    * Back to caching and invalidating commandIds, if we're having commands ephemeral it means we don't need to cache
      and invalidate, we can just save all commandIds because on restart all deleted commands wouldn't be ingested and
      only event.meta.commandId will be saved (aka actually effective command) but if system never restarts this still
      puts the risk of an incrementally increasing memory (as if it's a memory leak) not to mention the question whether
      we actually need to save every processed commandId or not; while it might be understandable for sagaSource
      eventIds since we know some commands might be regenerated on startup.

> Note: All the previous seems like a very complicated cat/mouse loop so far, and system needs more invariants
> verifications but also deep thinking sessions. 
