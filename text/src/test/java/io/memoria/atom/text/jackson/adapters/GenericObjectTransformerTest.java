package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.text.jackson.JacksonUtils;
import io.memoria.atom.text.jackson.JsonJackson;
import io.memoria.atom.text.jackson.adapters.generic.Person;
import io.memoria.atom.text.jackson.adapters.generic.SomeId;
import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericObjectTransformerTest {
  private static final JsonJackson json = new JsonJackson(createMapper());

  @Test
  void genericValueObjectDirectMapping() {
    // Given
    var jsonStr = "\"some_id\"";
    var obj = new SomeId("some_id");

    // When
    var serResult = json.serialize(obj).get();
    var desResult = json.deserialize(jsonStr, SomeId.class).get();

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  @Test
  void genericValueObjectInsideAnother() {
    // Given
    var jsonStr = """
            {
              "$type":"Person",
              "someId":"some_id",
              "name":"jack"
            }""";
    var obj = new Person(new SomeId("some_id"), "jack");

    // When
    var serResult = json.serialize(obj).get();
    var desResult = json.deserialize(jsonStr, Person.class).get();

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  private static ObjectMapper createMapper() {
    var subIdModule = JacksonUtils.genericValueObjectsModule(SomeId.class, SomeId::new, SomeId::myValue);
    var om = JacksonUtils.json(subIdModule);
    JacksonUtils.prettyJson(om);
    JacksonUtils.addMixInPropertyFormat(om, Person.class);
    return om;
  }
}
