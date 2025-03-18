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

Notes:

* `State` term is kind of used interchangeably with `Aggregate`, the reason for using state, is that the interfaces
  design allows state to be immutable, since aggregates usually have the command/evolution logic inside, and changing
  their state internally.

### 2.1 Id generation

Id generation can happen on so many levels from client to DB itself, pragmatically and after many trials, my decision
was moving id generation of events/commands to be in the rules (`saga`, `decider`, `evolver`), this allows control over
generation of Ids, while not leaving the responsibility to clients.

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

Events by nature should be persisted permanently, while permanent Commands persistence is optional

If permanent it means commands generated from saga events won't be missed and will be ingested everytime.

This has effect only on memory, since idempotency this is safe due to that ingested commands which produced events, the
commandsIds are loaded on startup.

Commands ephemeral persistence means, they'd need acknowledgement pattern otherwise they should not be deleted ever, but
there's a workaround though, which is to use **long** keep alive of such commands at the stream to keep the probability
of losing a command very low.

### 2.6 The case of Saga regeneration on startup (aka old events)

Saga is an evolving state machine, and as the application evolves we'd need to have a new command created based on
certain event.

### 2.7 Saga command idempotency

The Saga generated commands should be idempotent, but the issue here is that they're generated with new ids for example
when duplicated due to saga generation on startup mentioned previously, so in order to reach idempotency, we'll
propagate the only fact which is eventId hopping from the generated command eventually to the generated event, which
will require `Optional<EventId> sagaSource` should be added to the command meta and eventMeta.

For an empty pipeline scenario, the newly handled saga commands would contain `sagaSource`, and the sagaSource
eventId would be **propagated** to the generated saga events, when such events are re-ingested on startup they'd
fill up the sagaSource hashset bucket, this would allow any newly generated saga command to be checked at `decide()`
whether it has already produced an event.

The saga source eventIds hashset should be separate from normal eventId cache, because the idempotency here is for
commands not events.

* Deeper thought (can be skipped) it also prevents pipeline from cancelling the evolution of events who were added
  to sagaSource but not actually evolved if those sagaEvents were in same partition.
* This is good candidate for a written test to make sure of such separation

Another point is that the regeneration of saga commands in the init phase (aka re-ingesting of already persisted
events) **should be under a flag** since it generates all saga commands again as it's not ideal performance wise.

### 2.8 The decision of keeping all processed `eventIds`

Initially all processed `eventIds` were kept in hash sets, but I soon came to realise the
eventId can not be having wrong order due to redelivery it will just be delivered twice or more but consecutively,
meaning the partition could deliver(e3,e2,e1,e1) but not (e3,e1,e2,e1) this is guaranteed by any stream framework (e.g
kafka nats) that
messages in certain partitions are saved in same order they were produced, from this I needed to do some sort of
runtime compaction to avoid ingesting e1 twice, note that the sequence/version belongs to the event not the partition,
meaning aggregate events can be moved to any other partition.

As a result we were able to get rid of the `processedEventIds` hashset and only save the previous eventId, while also
checking
the aggregate event version making sure it's the right next event x state/aggregate.

### 2.8 The decision of in memory invalidation of `CommandIds` cahce

Initially I decided to use a **cache** for processed CommandIds to save some memory but this now seems like wrong
decision, first the cache invalidation puts the whole thing at risk, if we use size we're in the risk when we have big
partitions, and it would require manual intervention to increase cache size, and if we use time, what happens to idle
partitions, not to mention losing a design fact (command X **was** processed)

Another case in favor of not invalidating commandIds and invalidating being ephemeral which means after a restart it
would prune all previous
processed commands which were invalid, and only keep the ones who produced events.

Anyway such improvement still needs some investigation.
