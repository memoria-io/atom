package io.memoria.atom.eventsourcing.usecase.banking;

//import io.memoria.atom.core.eventsourcing.Command;
//import io.memoria.atom.core.eventsourcing.Domain;
//import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
//import io.memoria.atom.core.id.Id;

import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.core.text.TextTransformer;

import java.time.Duration;
//import java.util.UUID;

class PipelinesTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Duration timeout = Duration.ofMillis(300);
  // pipeline sharding
  private static final int oldPartitions = 3;
  private static final int newPartitions = 5;
  private static final String commandTopic = "commandTopic";
  private static final String oldEventTopic = "oldEventTopic";
  public static final String newEventTopic = "newEventTopic";
  // Pipelines
  //  private final ESStream ESStream;
  //  private final List<Pipeline<Account, AccountCommand, AccountEvent>> oldPipeline;
  //  private final List<Pipeline<Account, AccountCommand, AccountEvent>> pipeline1;

  PipelinesTest() {
    // Pipelines
    //    var oldRoutes = List.range(0, oldPartitions).map(PipelinesTest::odlRoute0);
    //    var newRoutes = List.range(0, newPartitions).map(PipelinesTest::route1);
    //    ESStream = new MemESStream(newRoutes.get().commandConfig().withHistory(100),
    //                               newRoutes.get().oldEventConfig(),
    //                               newRoutes.get().newEventConfig());
    //    oldPipeline = oldRoutes.map(this::createPipeline);
    //    pipeline1 = newRoutes.map(this::createPipeline);
  }

  //  @Test
  //  void sharding() {
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
  //    // And When
  //    StepVerifier.create(Flux.merge(pipeline1.map(Pipeline::run))).expectNextCount(eventCount).verifyTimeout(timeout);
  //
  //    // Then events are published to the new pipeline topic, with number of totalPartitions = totalPartitions,
  //    var newEvents = Flux.range(0, newPartitions).flatMap(i -> accountCreatedStream(newEventTopic, i));
  //    StepVerifier.create(newEvents).expectNextCount(eventCount).verifyTimeout(timeout);
  //  }
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
  //    StepVerifier.create(Flux.merge(pipeline1.map(Pipeline::runReduced)))
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
  //    ESStream.publish(DataSet.scenario(personsCount, nameChanges).map(PipelinesTest::toMsg))
  //            .delaySubscription(Duration.ofMillis(100))
  //            .subscribe();
  //    StepVerifier.create(Flux.merge(oldPipeline.map(Pipeline::run))).expectNextCount(eventCount).verifyTimeout(timeout);
  //  }
  //
  //  private Pipeline<Account, AccountCommand, AccountEvent> createPipeline(Route route) {
  //    return new Pipeline<>(stateDomain(), ESStream, transformer, route, LogConfig.FINE);
  //  }
  //
  //  private Domain<Account, AccountCommand, AccountEvent> stateDomain() {
  //    return new Domain<>(Account.class,
  //                        AccountCommand.class,
  //                        AccountEvent.class,
  //                        new AccountDecider(),
  //                        new AccountEvolver(),
  //                        new AccountReducer());
  //  }
  //
  //  private Flux<AccountEvent> accountCreatedStream(String topic, int i) {
  //    return ESStream.subscribe(topic, i, 0)
  //                   .concatMap(msg -> toMono(transformer.deserialize(msg.value(), AccountEvent.class)));
  //    //                 .doOnNext(e -> System.out.printf("p(%d)-%s%n", i, e));
  //  }
  //
  //  private static Route odlRoute0(int partition) {
  //    return new Route(commandTopic, partition, "dummy", 0, oldEventTopic, oldPartitions);
  //  }
  //
  //  private static Route route1(int partition) {
  //    return new Route(commandTopic, partition, oldEventTopic, oldPartitions, newEventTopic, newPartitions);
  //  }
  //
  //  private static ESStreamMsg toMsg(Command command) {
  //    var body = transformer.serialize(command).get();
  //    return new ESStreamMsg(commandTopic, command.partition(oldPartitions), Id.of(UUID.randomUUID()), body);
  //  }
}
