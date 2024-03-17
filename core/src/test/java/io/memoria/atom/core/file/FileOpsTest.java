package io.memoria.atom.core.file;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class FileOpsTest {
  private static final Logger log = LoggerFactory.getLogger(FileOpsTest.class.getName());

  private static final Path TEST_DIR = Path.of("/tmp/rFilesTest");
  private static final Path TEST_DIR_FILE_TXT = TEST_DIR.resolve("file.txt");

  @BeforeEach
  void beforeEach() throws IOException {
    FileOps.delete(TEST_DIR);
    FileOps.createDir(TEST_DIR);
  }

  @AfterEach
  void afterEach() throws IOException {
    FileOps.delete(TEST_DIR);
  }

  @Test
  void folderExists() {
    assertThat(Files.exists(TEST_DIR)).isTrue();
  }

  @Test
  @DisplayName("Should create parent dirs and write in file, then rewrite again")
  void createParents() throws IOException {
    // Given
    var filePath = TEST_DIR.resolve("childDir").resolve("grandChildDir").resolve("file.txt");
    // When
    assertThat(FileOps.write(filePath, "hello world")).isEqualTo(filePath);
    // Then
    assertThat(new String(Files.readAllBytes(filePath))).isEqualTo("hello world");
    // And when
    assertThat(FileOps.rewrite(filePath, "hello world again")).isEqualTo(filePath);
    // Then
    assertThat(new String(Files.readAllBytes(filePath))).isEqualTo("hello world again");
  }

  @Test
  void listFiles() throws IOException {
    // Given
    var p = FileOps.write(TEST_DIR.resolve("parent").resolve("file.txt"), "hello world");

    // When
    var str = Files.readString(p);
    var fileList = FileOps.listFiles(p.getParent());

    // Then
    Assertions.assertThat(str).isEqualTo("hello world");
    Assertions.assertThat(fileList).contains(p);
  }

  @Test
  void listDirectories() throws IOException {
    // Given
    var nDirs = 3;

    // When
    IntStream.range(0, nDirs)
             .mapToObj(i -> TEST_DIR.resolve(String.valueOf(i)))
             .forEach(dir -> createSomeFiles(dir, 1));

    // Then
    assertThat(FileOps.listDirectories(TEST_DIR)).hasSize(nDirs);
  }

  @Test
  void listIsEmpty() throws IOException {
    // Given
    FileOps.createDir(TEST_DIR);
    // When
    var listFiles = FileOps.listFiles(TEST_DIR);
    // Then
    assertThat(listFiles).isEmpty();
  }

  @Test
  void tryToDeleteRoot() {
    // Given
    var root = Path.of("/");

    // Then
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> FileOps.delete(root));
  }

  @Test
  @DisplayName("Delete directory and all sub directories and files")
  void deleteDirFilesAndSubDirs() throws IOException {
    // Given
    var nDirs = 3;
    var nFiles = 5;
    IntStream.range(0, nDirs)
             .mapToObj(i -> TEST_DIR.resolve(String.valueOf(i)))
             .forEach(dir -> createSomeFiles(dir, nFiles));
    assertThat(FileOps.listFiles(TEST_DIR.resolve("0"))).hasSize(nFiles);
    assertThat(FileOps.listDirectories(TEST_DIR)).hasSize(nDirs);

    // When
    FileOps.delete(TEST_DIR);
    // then
    assertThat(FileOps.listDirectories(TEST_DIR).size()).isEqualTo(0);
  }

  @Test
  void lastModifiedFile() throws IOException {
    // Given
    int nFiles = 10;
    var lastFileName = nFiles - 1 + ".json";
    createSomeFiles(TEST_DIR, nFiles);

    // When
    var lastModifiedFile = FileOps.lastModifiedFile(TEST_DIR);

    // Then
    assertThat(lastModifiedFile).isPresent();
    assertThat(lastModifiedFile.get()).isEqualTo(TEST_DIR.resolve(lastFileName));
  }

  @Test
  void read() throws IOException {
    // Given
    FileOps.createDir(TEST_DIR);
    Files.writeString(TEST_DIR_FILE_TXT, "welcome");
    // When
    var read = FileOps.read(TEST_DIR_FILE_TXT);
    // Then
    assertThat(read).isEqualTo("welcome");
  }

  @Test
  @DisplayName("Should create a new file")
  void rewrite() throws IOException {
    // When
    assertThat(FileOps.rewrite(TEST_DIR_FILE_TXT, "hello world")).isEqualTo(TEST_DIR_FILE_TXT);
    assertThat(FileOps.rewrite(TEST_DIR_FILE_TXT, "hi world")).isEqualTo(TEST_DIR_FILE_TXT);
    // Then
    var str = new String(Files.readAllBytes(TEST_DIR_FILE_TXT));
    assertThat(str).isEqualTo("hi world");
  }

  @Test
  @DisplayName("Should create a new file")
  void write() throws IOException {
    // When
    assertThat(FileOps.write(TEST_DIR_FILE_TXT, "hello world")).isEqualTo(TEST_DIR_FILE_TXT);
    // Then
    var str = new String(Files.readAllBytes(TEST_DIR_FILE_TXT));
    assertThat(str).isEqualTo("hello world");
  }

  private void createSomeFiles(Path path, int count) {
    try {
      for (int i : IntStream.range(0, count).toArray()) {
        var p = FileOps.write(path.resolve(i + ".json"), "hi" + i);
        log.debug("Written" + p);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
