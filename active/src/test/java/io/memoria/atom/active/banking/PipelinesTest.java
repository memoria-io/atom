package io.memoria.atom.active.banking;

class PipelinesTest {
  //  private static final TextTransformer transformer = new SerializableTransformer();
  //  private static final IdGenerator idGen = () -> Id.of(0);
  //  // Streams
  //  private final StreamConfig commandStreamConfig;
  //  private final CommandRepo<AccountCommand> commandStreamRepo;
  //  private final StreamConfig eventStreamConfig;
  //  private final EventRepo<AccountEvent> eventStreamRepo;
  //  // Pipeline
  //  private final BlockingPipeline<Account, AccountCommand, AccountEvent> pipeline;
  //
  //  PipelinesTest() {
  //    // Streams
  //    commandStreamConfig = new StreamConfig("commands", 0, 3);
  //    commandStreamRepo = new MemCommandRepo<>(commandStreamConfig, idGen, AccountCommand.class, transformer);
  //    eventStreamConfig = new StreamConfig("events", 0, 3);
  //    eventStreamRepo = new MemEventRepo<>(eventStreamConfig, idGen, AccountEvent.class, transformer);
  //    // Pipeline
  //    var stateDomain = new Domain<>(new Visitor(), new AccountDecider(), new AccountSaga(), new AccountEvolver());
  //    pipeline = new BlockingPipeline<>(stateDomain, commandStreamRepo, eventStreamRepo);
  //  }

  //    @Test
  //    void sharding() {
  //      // Given published commands
  //      int personsCount = 10;
  //      int nameChanges = 2;
  //      int eventCount = personsCount + (personsCount * nameChanges);
  //
  //      // When simple pipeline is activated
  //      runOldPipeline(personsCount, nameChanges, eventCount);
  //
  //      // Then events are published to old pipeline topic, with number of totalPartitions = prevPartitions
  //      var oldEvents = Flux.range(0, oldPartitions).flatMap(i -> accountCreatedStream(oldEventTopic, i));
  //      StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  //
  //      // And When
  //      StepVerifier.create(Flux.merge(pipeline1.map(StatePipeline::run)))
  //                  .expectNextCount(eventCount)
  //                  .verifyTimeout(timeout);
  //
  //      // Then events are published to the new pipeline topic, with number of totalPartitions = totalPartitions,
  //      var newEvents = Flux.range(0, newPartitions).flatMap(i -> accountCreatedStream(newEventTopic, i));
  //      StepVerifier.create(newEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  //    }
  //
  //  @Test
  //  void reduction() {
  //    // Given published commands
  //    int personsCount = 10;
  //    int nameChanges = 2;
  //    int eventCount = personsCount + (personsCount * nameChanges);
  //
  //    // When simple pipeline is activated
  //    runOldPipeline(personsCount, nameChanges, eventCount);
  //
  //    // Then events are published to old pipeline topic, with number of totalPartitions = prevPartitions
  //    var oldEvents = Flux.range(0, oldPartitions).flatMap(i -> accountCreatedStream(oldEventTopic, i));
  //    StepVerifier.create(oldEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  //
  //    // And When new pipelines are run with reduction
  //    StepVerifier.create(Flux.merge(pipeline1.map(StatePipeline::runReduced)))
  //                .expectNextCount(personsCount)
  //                .verifyTimeout(timeout);
  //
  //    /*
  //     * Then events are published to the new pipeline topic, with number of totalPartitions = totalPartitions,
  //     * and only one event per user
  //     */
  //    var newEvents = Flux.range(0, newPartitions).flatMap(i -> accountCreatedStream(newEventTopic, i));
  //    StepVerifier.create(newEvents).expectNextCount(personsCount).verifyTimeout(timeout);
  //  }
  //
  //  private void runOldPipeline(int personsCount, int nameChanges, int eventCount) {
  //    streamRepo.push(DataSet.scenario(personsCount, nameChanges).map(PipelinesTest::toMsg))
  //              .delaySubscription(Duration.ofMillis(100))
  //              .subscribe();
  //    StepVerifier.create(Flux.merge(oldPipeline.map(StatePipeline::run)))
  //                .expectNextCount(eventCount)
  //                .verifyTimeout(timeout);
  //  }

}
