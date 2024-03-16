package io.memoria.atom.core.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class ResourceFileOps {
  private ResourceFileOps() {}

  public static String read(String path) throws IOException {
    return resource(path);
  }

  public static List<String> readResourceOrFile(String path) throws IOException {
    if (path.startsWith("/")) {
      return fileLines(path);
    } else {
      return resourceLines(path);
    }
  }

  private static String resource(String path) throws IOException {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(path)) {
      return new String(Objects.requireNonNull(is).readAllBytes());
    }
  }

  private static List<String> fileLines(String path) throws IOException {
    try (var lines = Files.lines(Path.of(path))) {
      return lines.toList();
    }
  }

  private static List<String> resourceLines(String path) throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      return new BufferedReader(new InputStreamReader(inputStream)).lines().toList();
    }
  }
}
