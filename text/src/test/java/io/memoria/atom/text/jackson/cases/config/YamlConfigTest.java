package io.memoria.atom.text.jackson.cases.config;

import io.memoria.atom.text.jackson.TestDeps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigTest {
  @Test
  @DisplayName("App config nested values should be deserialized correctly")
  void appConfig() {
    String config = TestDeps.CONFIG_FILE_OPS.read("cases/config/yaml/AppConfigs.yaml").get();
    var appConfig = TestDeps.yaml.deserialize(config, AppConfig.class).get();
    assert appConfig != null;
    assertEquals("hello world", appConfig.subName());
    assertEquals(List.of("hi", "hello", "bye"), appConfig.subList());
  }
}
