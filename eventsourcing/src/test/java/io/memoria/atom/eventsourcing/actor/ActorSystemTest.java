package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.actor.system.ActorStore;
import io.memoria.atom.eventsourcing.actor.system.ActorSystem;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.rule.CreateState;
import io.memoria.atom.eventsourcing.rule.StateCreated;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ActorSystemTest {
  @Test
  void defaultSystem() {
    var actorStore = createStore();
    try (var actorSystem = ActorSystem.create(actorStore, new DomainActorFactory())) {
      // Given
      StateId stateId = StateId.of(0);
      var meta = new CommandMeta(CommandId.of(UUID.randomUUID()), stateId);
      // When
      var eventOpt = actorSystem.decide(stateId, new CreateState(meta));
      // Then
      assertThat(eventOpt).isPresent().containsInstanceOf(StateCreated.class);
      // And Given
      var event = eventOpt.get();
      // When
      var state = actorSystem.evolve(stateId, event);
      // Then
      assertThat(state).isPresent().hasValueSatisfying(s -> {
        assertThat(s.version()).isZero();
      });
    } catch (IOException | CommandException e) {
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

  private static ActorStore createStore() {
    return ActorStore.mapStore(new ConcurrentHashMap<>());
  }
}
