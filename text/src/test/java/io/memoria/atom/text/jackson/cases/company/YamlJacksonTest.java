package io.memoria.atom.text.jackson.cases.company;

import io.memoria.atom.text.jackson.Resources;
import io.memoria.atom.text.jackson.TestDeps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class YamlJacksonTest {

  @Test
  void serializeEngineer() throws IOException {
    // When
    var yamlEngineer = TestDeps.yaml.serialize(Resources.BOB_ENGINEER);

    // Then
    Assertions.assertThat(yamlEngineer).isEqualTo(Resources.BOB_ENGINEER_YAML);
  }

  @Test
  void serializeManager() throws IOException {
    // When
    var yamlEngineer = TestDeps.yaml.serialize(Resources.ANNIKA_MANAGER);

    // Then
    Assertions.assertThat(yamlEngineer).isEqualTo(Resources.ANNIKA_MANAGER_YAML);
  }

  @Test
  void toEngineer() throws IOException, ClassNotFoundException {
    // When
    var engineer = TestDeps.yaml.deserialize(Resources.BOB_ENGINEER_YAML, Engineer.class);

    // Then
    Assertions.assertThat(engineer).isEqualTo(Resources.BOB_ENGINEER);
  }

  @Test
  void toManager() throws IOException, ClassNotFoundException {
    // When
    var manager = TestDeps.yaml.deserialize(Resources.ANNIKA_MANAGER_YAML, Manager.class);
    // Then
    Assertions.assertThat(manager).isEqualTo(Resources.ANNIKA_MANAGER);
  }
}
