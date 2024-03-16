package io.memoria.atom.core.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class FileOps {

  private FileOps() {}

  // -------------------------------------------------------------------------
  // Create
  // -------------------------------------------------------------------------
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

  // -------------------------------------------------------------------------
  // Read
  // -------------------------------------------------------------------------
  public static List<Path> listAll(Path path) throws IOException {
    try (var l = Files.list(path)) {
      return l.toList();
    }
  }

  /**
   * @return list of all files in a path
   */
  public static List<Path> listFiles(Path path) throws IOException {
    try (var paths = Files.list(path)) {
      return paths.filter(f -> !Files.isDirectory(f)).sorted().toList();
    }
  }

  /**
   * @return list of directories inside a path
   */
  public static List<Path> listDir(Path path) throws IOException {
    try (var paths = Files.list(path)) {
      return paths.filter(f -> !Files.isDirectory(f)).sorted().toList();
    }
  }

  public static String read(Path path) throws IOException {
    return Files.readString(path);
  }

  public static List<String> readAsLines(Path path) throws IOException {
    try (var l = Files.lines(path)) {
      return l.toList();
    }
  }

  public static Optional<Path> lastModifiedFile(Path path) throws IOException {
    try (var l = Files.list(path)) {
      return l.reduce(FileOps::lastModifiedFile);
    }
  }

  public static Path lastModifiedFile(Path p1, Path p2) {
    return (p1.toFile().lastModified() > p2.toFile().lastModified()) ? p1 : p2;
  }

  // -------------------------------------------------------------------------
  // Delete
  // -------------------------------------------------------------------------

  public static void deleteDirFiles(Path path) throws IOException {
    try (var stream = Files.list(path)) {
      for (Path p : stream.toList()) {
        Files.deleteIfExists(p);
      }
    }
  }

  public static void deleteDir(Path path) throws IOException {
    deleteDirFiles(path);
    for (Path p : listDir(path)) {
      deleteDir(p);
    }
    Files.delete(path);
  }
}
