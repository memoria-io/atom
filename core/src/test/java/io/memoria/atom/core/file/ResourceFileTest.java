package io.memoria.atom.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ResourceFileTest {
  private static final String TEST_DIR = "file/resourceFileOps/";

  @Test
  void readResource() throws IOException {
    // Given
    String path = TEST_DIR + "resourceFile.yaml";

    // When
    var str = ResourceFile.of(path).read();

    // Then
    Assertions.assertEquals("hello world\nbye bye", str);
  }
}
