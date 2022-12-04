package io.memoria.atom.active.eventsourcing.banking;

import io.memoria.atom.active.eventsourcing.banking.command.CloseAccount;
import io.memoria.atom.active.eventsourcing.banking.command.CreateAccount;
import io.memoria.atom.active.eventsourcing.banking.command.UserCommand;
import io.memoria.atom.active.eventsourcing.banking.event.UserEvent;
import io.memoria.atom.active.eventsourcing.banking.state.User;
import io.memoria.atom.active.eventsourcing.banking.state.Visitor;
import io.memoria.atom.active.eventsourcing.pipeline.Domain;
import io.memoria.atom.active.eventsourcing.pipeline.Pipeline;
import io.memoria.atom.active.eventsourcing.pipeline.Route;
import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.stream.CommandStream;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class PipelineTest {
  private static final Route route = new Route("cmdTopic", 0, 1, "eventTopic");
  private final CommandStream<UserCommand> commandStream;
  private final EventRepo<UserEvent> eventRepo;
  private final Pipeline<UserCommand, UserEvent> pipeline;

  PipelineTest() {
    var domain = new Domain<>(User.class,
                              UserCommand.class,
                              UserEvent.class,
                              new Visitor(),
                              new AccountDecider(),
                              new AccountSaga(),
                              new AccountEvolver());

    commandStream = CommandStream.fromMemory(route.cmdTopic(), route.totalCmdPartitions());
    eventRepo = EventRepo.from(route.eventTopic());
    pipeline = Pipeline.create(domain, route, commandStream, eventRepo);
  }

  @Test
  void simple() {
    // Given
    var bobId = StateId.of("bob");
    var janId = StateId.of("jan");
    var createBob = CreateAccount.of(bobId, "bob", 100);
    //    var createJan = CreateAccount.of(janId, "jan", 100);
    var sendMoneyFromBobToJan = DataSet.createTransfer(bobId, janId, 50);
    var sendSecondMoney = DataSet.createTransfer(bobId, janId, 25);
    //    var sendThirdMoney = DataSet.createTransfer(bobId, janId, 25);
    var closeJanAccount = CloseAccount.of(bobId);
    // When
    Stream<UserCommand> cmds = Stream.of(createBob, sendMoneyFromBobToJan, sendSecondMoney, closeJanAccount);

    cmds.map(pipeline::append).forEach(Try::get);
    // Then
    final AtomicInteger latch = new AtomicInteger(4);
    pipeline.stream().takeWhile(s -> latch.decrementAndGet() > 0).forEach(Try::get);
    //    eventRepo.stream(bobId).map(Try::get).forEach(System.out::println);
    //    Thread.sleep(1000);
    //    Thread.currentThread().join();
    //    assert size == 5;
    //    var accounts = this.pipeline.run(commandStreamRepo.stream()).toList();
    //    Assertions.assertInstanceOf(Acc.class, accounts.get(0));
    //    Assertions.assertInstanceOf(ClosedAccount.class, accounts.get(1));
  }

  //
  //  @Test
  //  void complex() {
  //    // Given
  //    int nAccounts = 4;
  //    int balance = 100;
  //    int treasury = nAccounts * balance;
  //    var createAccounts = DataSet.createAccounts(nAccounts, balance);
  //    var accountIds = createAccounts.map(AccountCommand::accountId);
  //    var randomOutbounds = DataSet.randomOutBounds(nAccounts, balance);
  //    var cmds = Flux.<Command>fromIterable(createAccounts)
  //                   .concatWith(Flux.fromIterable(randomOutbounds))
  //                   .map(this::toMsg);
  //
  //    // When
  //    var pipelines = Flux.merge(streamRepo.push(cmds), statePipeline.run(), blockingSagaPipeline.run());
  //    StepVerifier.create(pipelines).expectNextCount(20).verifyTimeout(timeout);
  //    // Then
  //    var accounts = accountIds.map(statePipeline::stateOrInit).map(u -> (Acc) u);
  //    Assertions.assertEquals(nAccounts, accounts.size());
  //    var total = accounts.foldLeft(0, (a, b) -> a + b.balance());
  //    Assertions.assertEquals(treasury, total);
  //  }
  //
  //  private SagaDomain<AccountEvent, AccountCommand> sagaDomain() {
  //    return new SagaDomain<>(AccountEvent.class, AccountCommand.class, new AccountSagaDecider());
  //  }
  //
  //  private StateDomain<Account, AccountCommand, AccountEvent> stateDomain() {
  //    return new StateDomain<>(Account.class,
  //                             AccountCommand.class,
  //                             AccountEvent.class,
  //                             new Visitor(),
  //                             new AccountStateDecider(),
  //                             new AccountStateEvolver(),
  //                             new AccountEventReducer());
  //  }
  //
  //  private Msg toMsg(Command command) {
  //    var body = transformer.serialize(command).get();
  //    return new Msg(route.commandTopic(), route.partition(), Id.of(UUID.randomUUID()), body);
  //  }
}
