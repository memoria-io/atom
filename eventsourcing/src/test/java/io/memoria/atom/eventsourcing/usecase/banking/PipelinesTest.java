package io.memoria.atom.eventsourcing.usecase.banking;

import io.memoria.atom.core.id.Id;
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
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class PipelinesTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final CommandRoute route = new CommandRoute("events", 0, 1, "commands", 0, 1);
  private final CommandPipeline<Account, AccountCommand, AccountEvent> pipeline = createPipeline();

  @Test
  void balance() {
    // Given
    var nAccounts = 10;
    int initialBalance = 500;
    int creditBalance = 300;
    int endBalance = initialBalance + creditBalance;

    var createAccounts = DataSet.createAccounts(nAccounts, initialBalance);
    var creditAccounts = DataSet.credit(Id.of("SomeFakeDebitId"), nAccounts, creditBalance);
    creditAccounts = createAccounts.appendAll(creditAccounts); // Handling duplicates (at least once messaging)
    var commands = Flux.fromIterable(createAccounts).concatWith(Flux.fromIterable(creditAccounts));

    // When
    StepVerifier.create(pipeline.handle(commands)).expectNextCount(nAccounts * 2).verifyComplete();

    // Then
    List.range(0, nAccounts).forEach(id -> verifyBalance(id, endBalance));
  }

  private void verifyBalance(int accountId, int endBalance) {
    var account0Events = pipeline.sub().filter(e -> e.stateId().equals(DataSet.accountId(accountId))).take(2);
    var account0Mono = pipeline.domain.evolver().reduce(account0Events).map(acc -> (OpenAccount) acc);
    StepVerifier.create(account0Mono).expectNextMatches(acc -> acc.balance() == endBalance).verifyComplete();
  }

  private CommandPipeline<Account, AccountCommand, AccountEvent> createPipeline() {
    return new CommandPipeline<>(stateDomain(), route, createMsgStream(), KVStore.inMemory(), transformer);
  }

  private static ESMsgStream createMsgStream() {
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
