package io.memoria.atom.core.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class FileOps {

  private FileOps() {}

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void createDir(Path path) {
    path.toFile().mkdirs();
  }

  public static Path rewrite(Path path, String content) throws IOException {
    Files.createDirectories(path.getParent());
    return Files.writeString(path, content, TRUNCATE_EXISTING, CREATE);
  }

  public static Path write(Path path, String content) throws IOException {
    Files.createDirectories(path.getParent());
    return Files.writeString(path, content, CREATE_NEW);
  }

  /**
   * @return list of all files in a path
   */
  public static List<Path> listFiles(Path path) throws IOException {
    if (!Files.exists(path)) {
      return List.of();
    }
    try (var paths = Files.list(path)) {
      return paths.filter(f -> !Files.isDirectory(f)).sorted().toList();
    }
  }

  /**
   * @return list of directories inside a path
   */
  public static List<Path> listDirectories(Path path) throws IOException {
    if (!Files.exists(path)) {
      return List.of();
    }
    try (var paths = Files.list(path)) {
      return paths.filter(Files::isDirectory).sorted().toList();
    }
  }

  public static String read(Path path) throws IOException {
    return Files.readString(path);
  }

  public static Optional<Path> lastModifiedFile(Path path) throws IOException {
    try (var paths = Files.list(path)) {
      return paths.reduce(FileOps::lastModifiedFile);
    }
  }

  public static Path lastModifiedFile(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  public static void delete(Path path) throws IOException {
    if (path.isAbsolute() && path.toString().equals("/")) {
      throw new IllegalArgumentException("Can't delete root directory");
    }
    if (Files.isDirectory(path)) {
      try (Stream<Path> stream = Files.list(path)) {
        for (Path file : stream.toList()) {
          delete(file);
        }
      }
    }
    Files.deleteIfExists(path);
  }

  public static String readResource(String path) throws IOException {
    try (var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path))) {
      var reader = new BufferedReader(new InputStreamReader(inputStream));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
  }

  public static Stream<String> readResourceLines(String path) throws IOException {
    var inputStream = Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(path));
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }
}
