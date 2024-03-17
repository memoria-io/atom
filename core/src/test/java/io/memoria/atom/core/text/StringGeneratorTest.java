package io.memoria.atom.core.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.stream.IntStream;

class StringGeneratorTest {
  @Test
  void randomAlphanumericTest() {
    // Given
    SecureRandom random = new SecureRandom();
    TextGenerator ru = new TextGenerator(random);
    IntStream.range(0, 100).forEach(_ -> {
      // When
      int min = random.nextInt(10);
      int max = min + 200;

      // Then
      Assertions.assertEquals(ru.alphanumeric(max).length(), max);
      Assertions.assertTrue(ru.minMaxAlphanumeric(min, max).length() <= max);
      Assertions.assertTrue(ru.minMaxAlphanumeric(min, max).length() >= min);
    });
  }

  @Test
  void randomHexTest() {
    // Given
    SecureRandom random = new SecureRandom();
    TextGenerator ru = new TextGenerator(random);
    IntStream.range(0, 100).forEach(_ -> {
      // When
      int min = random.nextInt(10);
      int max = min + 200;

      // Then
      Assertions.assertEquals(ru.hex(max).length(), max);
      Assertions.assertTrue(ru.minMaxHex(min, max).length() <= max);
      Assertions.assertTrue(ru.minMaxHex(min, max).length() >= min);
      Assertions.assertFalse(ru.minMaxHex(min, max).contains("g") || ru.minMaxHex(min, max).contains("h"));
    });
  }
}
