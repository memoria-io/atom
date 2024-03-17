package io.memoria.atom.core.file;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

class ConfigFileOpsTest {
  private static final String TEST_DIR = "file/configFileOps/";
  private final ConfigFileOps configOps;

  ConfigFileOpsTest() {
    System.setProperty("MY_SYSTEM_PROPERTY", "2000");
    configOps = new ConfigFileOps("#include:", true);
  }

  @ParameterizedTest
  @MethodSource("paths")
  @DisplayName("should read the nested files")
  void readNestedFile(String path) throws IOException {
    // Given
    var expectedConfig = ResourceFile.of(TEST_DIR + "expectedConfig.yaml").read();

    // When
    var configFile = configOps.read(path);

    // Then
    Assertions.assertThat(configFile).isEqualTo(expectedConfig);
  }

  @Test
  void readSystemEnv() throws IOException {
    // When
    var file = configOps.read(TEST_DIR + "systemEnv.yaml");
    // Then
    var lines = file.split("\n");
    Assertions.assertThat(lines[0]).isNotEqualTo("javaHomePath: /hello/java");
    Assertions.assertThat(lines[1]).isEqualTo("otherValue: defaultValue");
    Assertions.assertThat(lines[2]).isEqualTo("routeValue: /defaultValue/{paramName}/someOther");
    Assertions.assertThat(lines[3]).isEqualTo("routeValueWithSpace: /defaultValue/{paramName}/someOther");
    Assertions.assertThat(lines[4]).startsWith("javaVersionSystemProperty: 21");
    Assertions.assertThat(lines[5]).isEqualTo("mySysProp: 2000");
  }

  private static Stream<String> paths() {
    var path = TEST_DIR + "Config.yaml";
    var rootPath = ClassLoader.getSystemResource(path).getPath();
    return Stream.of(path, rootPath);
  }
}
