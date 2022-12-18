package io.memoria.atom.text.jackson.cases.company;

import io.memoria.atom.text.jackson.Resources;
import io.memoria.atom.text.jackson.TestDeps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineer = TestDeps.yaml.serialize(Resources.BOB_ENGINEER).get();
    assert Resources.BOB_ENGINEER_YAML != null;
    assertEquals(Resources.BOB_ENGINEER_YAML, yamlEngineer);
  }

  @Test
  void serializeManager() {
    var yamlEngineer = TestDeps.yaml.serialize(Resources.ANNIKA_MANAGER).get();
    assert Resources.ANNIKA_MANAGER_YAML != null;
    assertEquals(Resources.ANNIKA_MANAGER_YAML, yamlEngineer);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = TestDeps.yaml.deserialize(Resources.BOB_ENGINEER_YAML, Engineer.class).get();
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void toManager() {
    // When
    var manager = TestDeps.yaml.deserialize(Resources.ANNIKA_MANAGER_YAML, Manager.class).get();
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
  }
}
