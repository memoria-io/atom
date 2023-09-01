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

### 2.3 EventMeta and CommandMeta

Initially we only had EventId which was even a simple string, but with more additions like timestamp and referencing
commandId and stateId etc, it was time to group in as a meta information.

### 2.3 Saga based commands, and the SagaSource addition to meta

* Published commands especially saga ones should be at least quorum persisted, to make sure they're not missed.
* The Saga generated commands should be idempotent as well, to allow their regeneration by ingesting old events, this
  way if there
  was human error in the eventsourcing state machine, it could be fixed for old events as well
    * In order to do that, `Option<EventId> sagaSource` should be added to the command meta, so that when ingesting the
      command pipeline the commands are checked if they have same source and not executed again, this is because Saga
      commands would have new Ids and the only constant is the saga source (eventId) which generated them.
    * The saga source eventIds cache should be separate from normal eventId cache, because the idempotency here is for
      commands
      not events.
        * This is good candidate for a written test to make sure of such separation
    * The regeneration of saga commands in the init phase (aka re-ingesting of already persisted events) should be under
      a flag since it generates all saga commands again and it's not ideal (since each event that produces saga will be
      now generating a command again) to do such thing despite it's possible and safe due
      to commands idempotency guaranteed as mentioned previously
    
