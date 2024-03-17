package io.memoria.atom.core.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceFile {
  private final String path;

  private ResourceFile(String path) {
    this.path = path;
  }

  public static ResourceFile of(Path path) {
    return new ResourceFile(path.toString());
  }

  public static ResourceFile of(String path) {
    return new ResourceFile(path);
  }

  public String read() throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      var reader = new BufferedReader(new InputStreamReader(inputStream));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  public List<String> readLines() throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      return new BufferedReader(new InputStreamReader(inputStream)).lines().toList();
    }
  }
}
