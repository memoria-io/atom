package io.memoria.atom.core.file;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ConfigFileTest {
  ConfigFileTest() {
    System.setProperty("MY_SYSTEM_PROPERTY", "2000");
  }

  @Test
  @DisplayName("should read the nested files")
  void readNestedFile() throws IOException {
    // Given
    var configFile = new ConfigFile("file/configFileOps/Config.yaml");
    var expected = FileOps.readResource("file/configFileOps/expectedConfig.yaml");

    // When
    var file = configFile.read();

    // Then
    Assertions.assertThat(file).isEqualTo(expected);
  }

  @Test
  void readSystemEnv() throws IOException {
    // When
    var lines = new ConfigFile("file/configFileOps/systemEnv.yaml").readLines().toList();

    // Then
    Assertions.assertThat(lines.get(0)).isNotEqualTo("javaHomePath: /hello/java");
    Assertions.assertThat(lines.get(1)).isEqualTo("otherValue: defaultValue");
    Assertions.assertThat(lines.get(2)).isEqualTo("routeValue: /defaultValue/{paramName}/someOther");
    Assertions.assertThat(lines.get(3)).isEqualTo("routeValueWithSpace: /defaultValue/{paramName}/someOther");
    Assertions.assertThat(lines.get(4)).startsWith("javaVersionSystemProperty: 24");
    Assertions.assertThat(lines.get(5)).isEqualTo("mySysProp: 2000");
  }
}
