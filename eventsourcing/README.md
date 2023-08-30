# Eventsourcing

## Saga based commands, and the SagaSource addition to meta

* Published commands especially saga ones should be at least quorum persisted, to make sure they're not missed.
* The Saga commands should be idempotent as well, to allow their regeneration by ingesting old events, this way if there
  was human error in the eventsourcing state machine, it could be fixed for old events as well
    * In order to do that, `Option<EventId> sagaSource` should be added to the command meta, so that when ingesting the
      command pipeline the commands are checked if they have same source and not executed again, this is because Saga
      commands are generated each time and the only constant is the eventId which generated them.
    * The saga source eventIds cache should be separate from eventId cache, because the idempotency here is for commands
      not events.
        * This is good candidate for a written test to make sure of such separation
    * The regeneration of saga commands in the init phase (aka re-ingesting of already persisted events) should be under
      a flag since it generates all saga commands again and it's not ideal (since each event that produces saga will be
      now generating a command again) to do such thing despite it's possible and safe due
      to commands idempotency guaranteed as mentioned previously
    
