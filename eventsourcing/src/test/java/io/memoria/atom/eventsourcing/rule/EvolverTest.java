package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.CommandId;
import org.junit.jupiter.api.Test;

class EvolverTest {
  @Test
  void dummyTest(){
    assert new CommandId("id").toString().equals("id");
  }
}
