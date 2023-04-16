package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.core.repo.KVStore;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Domain;
import io.memoria.atom.eventsourcing.pipeline.CommandPipeline;
import io.memoria.atom.eventsourcing.pipeline.CommandRoute;
import io.memoria.atom.eventsourcing.usecase.banking.command.AccountCommand;
import io.memoria.atom.eventsourcing.usecase.banking.event.AccountEvent;
import io.memoria.atom.eventsourcing.usecase.banking.state.Account;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class PipelinesTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final CommandRoute route = new CommandRoute("events", 0, 1, "commands", 0, 1);
  // pipeline
  private final CommandPipeline<Account, AccountCommand, AccountEvent> pipeline = createPipeline();

  @Test
  void createAccountsAndChangeNames() {
    // Given
    int personsCount = 10;
    int nameChanges = 2;
    int expectedEventCount = personsCount + (personsCount * nameChanges);
    var scenarioCommands = DataSet.scenario(personsCount, nameChanges);

    // When
    var p = pipeline.handle(scenarioCommands);

    // Then
    StepVerifier.create(p).expectNextCount(expectedEventCount).verifyComplete();
  }

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
  private CommandPipeline<Account, AccountCommand, AccountEvent> createPipeline() {
    return new CommandPipeline<>(stateDomain(), route, createMsgStream(route), KVStore.inMemory(), transformer);
  }

  private static ESMsgStream createMsgStream(CommandRoute route) {
    var topics = HashMap.of(route.cmdTopic(),
                            route.cmdTotalPartitions(),
                            route.eventTopic(),
                            route.eventTotalPartitions());
    return ESMsgStream.inMemory(topics);
  }

  private static Domain<Account, AccountCommand, AccountEvent> stateDomain() {
    return new Domain<>(Account.class,
                        AccountCommand.class,
                        AccountEvent.class,
                        new AccountDecider(),
                        new AccountEvolver(),
                        new AccountSaga());
  }
}
