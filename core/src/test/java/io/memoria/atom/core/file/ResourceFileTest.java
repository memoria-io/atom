package io.memoria.atom.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ResourceFileTest {
  @Test
  void readResource() throws IOException {
    // Given
    String path = "file/resourceFileOps/resourceFile.yaml";

    // When
    var str = FileOps.readResource(path);
    ;

    // Then
    Assertions.assertEquals("hello world\nbye bye", str);
  }
}
