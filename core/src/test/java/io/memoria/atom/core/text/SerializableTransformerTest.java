package io.memoria.atom.core.text;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializableTransformerTest {
  @Test
  void serializableTest() throws TextException {
    // Given
    var transformer = new SerializableTransformer();
    var personObj = new Person("bob", 19, new Location(10, 20));

    // When
    var str = transformer.serialize(personObj);
    var obj = transformer.deserialize(str, Person.class);

    // Then
    Assertions.assertThat(personObj).isEqualTo(obj);
    Assertions.assertThat(personObj.i()).isEmpty();
  }
}
