package io.memoria.atom.core.text;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializableTransformerTest {
  @Test
  void serializableTest() throws IOException, ClassNotFoundException {
    // Given
    var transformer = new SerializableTransformer();
    var personObj = new Person("bob", 19, new Location(10, 20));

    // When
    var str = transformer.serialize(personObj);
    var obj = transformer.deserialize(str, Person.class);

    // Then
    assertEquals(personObj, obj);
  }
}
