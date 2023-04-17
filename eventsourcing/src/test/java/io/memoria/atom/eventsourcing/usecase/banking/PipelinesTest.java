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

  @Test
  void withInitialEvents() {
    // Given published commands
    int personsCount = 10;
    int nameChanges = 2;
    int expectedEventCount = personsCount + (personsCount * nameChanges);
    var scenarioCommands = DataSet.scenario(personsCount, nameChanges);

    // When
    var p = pipeline.handle(scenarioCommands);
    StepVerifier.create(p).expectNextCount(expectedEventCount).verifyComplete();

    // Then

  }

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
