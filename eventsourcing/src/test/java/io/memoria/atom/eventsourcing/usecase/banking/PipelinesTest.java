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
import io.memoria.atom.eventsourcing.usecase.banking.state.OpenAccount;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

class PipelinesTest {
  private static final TextTransformer transformer = new SerializableTransformer();
  private static final CommandRoute route = new CommandRoute("commands", "events", 0, 1);
  private final CommandPipeline<Account, AccountCommand, AccountEvent> pipeline = createPipeline();

  @Test
  void simpleCredit() {
    // Given
    var nAccounts = 10;
    int initialBalance = 500;
    int creditBalance = 300;
    int endBalance = initialBalance + creditBalance;

    var createAccounts = DataSet.createAccounts(nAccounts, initialBalance);
    var creditAccounts = DataSet.credit(nAccounts, creditBalance);
    creditAccounts = createAccounts.appendAll(creditAccounts); // Handling duplicates (at least once messaging)
    var commands = Flux.fromIterable(createAccounts).concatWith(Flux.fromIterable(creditAccounts));

    // When
    StepVerifier.create(pipeline.handle(commands)).expectNextCount(nAccounts * 2).verifyComplete();

    // Then
    List.range(0, nAccounts).map(this::account).forEach(id -> verifyBalance(id, endBalance));
  }

  @Test
  void sagaDebit() {
    // Given
    var nAccounts = 10;
    int initialBalance = 500;
    int debitBalance = 300;
    int expectedDebitedAccountsBalance = initialBalance - debitBalance;
    int expectedCreditedAccountsBalance = initialBalance + debitBalance;
    int expectedTotalBalance = nAccounts * initialBalance;

    var createAccounts = DataSet.createAccounts(nAccounts, initialBalance);
    var debitAccounts = DataSet.debit(nAccounts, debitBalance);
    var commands = Flux.fromIterable(createAccounts).concatWith(Flux.fromIterable(debitAccounts));
    commands.flatMap(pipeline.commandStream::pub).subscribe();

    // When
    StepVerifier.create(pipeline.handle()).expectNextCount(25).expectTimeout(Duration.ofMillis(100)).verify();

    // Then
    List.range(0, nAccounts / 2).map(this::account).forEach(id -> verifyBalance(id, expectedDebitedAccountsBalance));

    List.range(nAccounts / 2, nAccounts)
        .map(this::account)
        .forEach(id -> verifyBalance(id, expectedCreditedAccountsBalance));

    var totalBalance = List.range(0, nAccounts)
                           .map(this::account)
                           .map(Mono::block)
                           .foldLeft(0, (a, b) -> a + b.balance());
    Assertions.assertThat(totalBalance).isEqualTo(expectedTotalBalance);
  }

  private void verifyBalance(Mono<OpenAccount> account, int endBalance) {
    StepVerifier.create(account).expectNextMatches(acc -> acc.balance() == endBalance).verifyComplete();
  }

  private Mono<OpenAccount> account(int accountId) {
    var account0Events = pipeline.sub().filter(e -> e.stateId().equals(DataSet.accountId(accountId))).take(2);
    return pipeline.domain.evolver().reduce(account0Events).map(acc -> (OpenAccount) acc);
  }

  private CommandPipeline<Account, AccountCommand, AccountEvent> createPipeline() {
    return new CommandPipeline<>(stateDomain(), route, createMsgStream(), KVStore.inMemory(), transformer);
  }

  private static ESMsgStream createMsgStream() {
    return ESMsgStream.inMemory(Map.of(route.cmdTopic(),
                                       route.totalPartitions(),
                                       route.eventTopic(),
                                       route.totalPartitions()));
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
