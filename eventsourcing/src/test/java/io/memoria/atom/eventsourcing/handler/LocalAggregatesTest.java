package io.memoria.atom.eventsourcing.handler;

import io.memoria.atom.eventsourcing.handler.store.Store;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.rule.CreateState;
import io.memoria.atom.eventsourcing.rule.StateCreated;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalAggregatesTest {
  @Test
  void defaultSystem() {
    var actorStore = createStore();
    try {
      // Given
      var actorSystem = LocalAggregates.create(actorStore, new DomainAggregateFactory());
      StateId stateId = StateId.of(0);
      var meta = new CommandMeta(CommandId.of(UUID.randomUUID()), stateId);
      // When
      var eventOpt = actorSystem.decide(stateId, new CreateState(meta));
      // Then
      assertThat(eventOpt).isPresent().containsInstanceOf(StateCreated.class);
      // And Given
      var event = eventOpt.get();
      // When
      var state = actorSystem.init(stateId, event);
      // Then
      assertThat(state).isPresent().hasValueSatisfying(s -> {
        assertThat(s.version()).isZero();
      });
    } catch (CommandException e) {
      throw new RuntimeException(e);
    }
  }

  //  private static ActorStore cachedActorStore() {
  //    var config = new MutableConfiguration<StateId, StateAggregate>().setTypes(StateId.class, StateAggregate.class)
  //                                                                    .setStoreByValue(false);
  //    //.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
  //    var cache = Caching.getCachingProvider().getCacheManager().createCache("simpleCache", config);
  //    return ActorStore.cacheStore(cache);
  //  }

  private static Store createStore() {
    return Store.mapStore(new ConcurrentHashMap<>());
  }
}
