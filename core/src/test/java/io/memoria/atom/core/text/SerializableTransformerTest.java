package io.memoria.atom.core.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializableTransformerTest {
  @Test
  void serializableTest() {
    var serTrans = new SerializableTransformer();
    var personObj = new Person("bob", 19, new Location(10, 20));
    var str = serTrans.serialize(personObj).get();
    var obj = serTrans.deserialize(str, Person.class).get();
    assertEquals(personObj, obj);
  }
}
