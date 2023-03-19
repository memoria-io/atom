package io.memoria.atom.active.eventsourcing.aggregate;

import io.memoria.atom.active.eventsourcing.banking.AccountDecider;
import io.memoria.atom.active.eventsourcing.banking.AccountEvolver;
import io.memoria.atom.active.eventsourcing.banking.AccountSaga;
import io.memoria.atom.core.eventsourcing.Domain;
import io.memoria.atom.core.eventsourcing.Route;
import io.memoria.atom.active.eventsourcing.adapter.stream.CommandStream;
import io.memoria.atom.active.eventsourcing.adapter.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.banking.command.CreateAccount;
import io.memoria.atom.active.eventsourcing.banking.command.UserCommand;
import io.memoria.atom.active.eventsourcing.banking.event.UserEvent;
import io.memoria.atom.active.eventsourcing.banking.state.User;
import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.text.SerializableTransformer;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DispatcherTest {
  private static final Logger log = LoggerFactory.getLogger(DispatcherTest.class.getSimpleName());

  private static final TextTransformer transformer = new SerializableTransformer();
  private static final Route route = new Route("commands_topic", 0, 1, "events_topic");
  private static final Domain<User, UserCommand, UserEvent> domain = createdomain();
  private final static ESStream esStream = ESStream.inMemory(route.cmdTopic(), route.totalCmdPartitions());
  private final static ESRepo esRepo = ESRepo.inMemory(route.eventTable());

  private final CommandStream<UserCommand> cmdStream;
  private final EventRepo<UserEvent> eventRepo;

  DispatcherTest() {
    cmdStream = CommandStream.create(route, esStream, transformer, domain.cClass());
    eventRepo = EventRepo.create(route, esRepo, transformer, domain.eClass());
  }

  @Test
  void twoAccounts() throws InterruptedException {
    // Given
    var bobId = StateId.of("bob");
    var janId = StateId.of("jan");

    // When
    twoAccountsCommands(bobId, janId).map(cmdStream::pub).forEach(Try::get);
    try (var dispatcher = createDispatcher()) {
      assert dispatcher.run().limit(5).count() == 5;
      var bobEventsCount = 0L;
      var loops = 0;
      var bobEvents = eventRepo.getAll(bobId).toList();
      while (bobEventsCount < 4) {
        loops++;
        // Expected to have max 10 loops until results are achieved
        assertThat(loops).isLessThan(10);
        Thread.sleep(10);
        bobEvents = eventRepo.getAll(bobId).toList();
        bobEventsCount = bobEvents.size();
      }
      // Then
      assertThat(bobEvents).allMatch(e -> e.get().stateId().equals(bobId));
    }
  }

  @Test
  void multipleAccounts() {
    // Given
    int nAccounts = 4;
    int balance = 100;
    int treasury = nAccounts * balance;
    var createAccounts = DataSet.createAccounts(nAccounts, balance);
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
  }

  public Stream<UserCommand> twoAccountsCommands(StateId bobId, StateId janId) {
    var createBob = CreateAccount.of(bobId, "bob", 100);
    var createJan = CreateAccount.of(janId, "jan", 100);
    var sendMoneyFromBobToJan = DataSet.createTransfer(bobId, janId, 50);
    var sendSecondMoney = DataSet.createTransfer(bobId, janId, 25);
    var sendThirdMoney = DataSet.createTransfer(bobId, janId, 25);
    return Stream.of(createBob, createJan, sendMoneyFromBobToJan, sendSecondMoney, sendThirdMoney);
  }

  private static Domain<User, UserCommand, UserEvent> createdomain() {
    return new Domain<>(User.class,
                        UserCommand.class,
                        UserEvent.class,
                        new AccountDecider(),
                        new AccountSaga(),
                        new AccountEvolver());
  }

  private static Dispatcher<User, UserCommand, UserEvent> createDispatcher() {
    return new Dispatcher<>(domain, route, esStream, esRepo, transformer, tr -> log.info(tr.toString()));
  }
}
