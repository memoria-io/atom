package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.text.jackson.Resources;
import org.junit.jupiter.api.Test;

import static io.memoria.reactive.text.jackson.TestDeps.yaml;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlJacksonTest {

  @Test
  void serializeEngineer() {
    var yamlEngineer = yaml.serialize(Resources.BOB_ENGINEER).get();
    assert Resources.BOB_ENGINEER_YAML != null;
    assertEquals(Resources.BOB_ENGINEER_YAML, yamlEngineer);
  }

  @Test
  void serializeManager() {
    var yamlEngineer = yaml.serialize(Resources.ANNIKA_MANAGER).get();
    assert Resources.ANNIKA_MANAGER_YAML != null;
    assertEquals(Resources.ANNIKA_MANAGER_YAML, yamlEngineer);
  }

  @Test
  void toEngineer() {
    // When
    var engineer = yaml.deserialize(Resources.BOB_ENGINEER_YAML, Engineer.class).get();
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void toManager() {
    // When
    var manager = yaml.deserialize(Resources.ANNIKA_MANAGER_YAML, Manager.class).get();
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
  }
}
