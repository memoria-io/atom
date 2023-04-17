package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.text.jackson.JacksonUtils;
import io.memoria.atom.text.jackson.JsonJackson;
import io.memoria.atom.text.jackson.adapters.id.AnotherId;
import io.memoria.atom.text.jackson.adapters.id.Person;
import io.memoria.atom.text.jackson.adapters.id.SomeId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdTransformerTest {
  private static final JsonJackson json = new JsonJackson(createMapper());

  @Test
  void idSubTypesDirectMapping() {
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
  void idSubTypesInObject() {
    // Given
    var jsonStr = """
            {
              "$type":"Person",
              "id":"0",
              "someId":"some_id",
              "anotherId":"another_id",
              "name":"jack"
            }""";
    var obj = new Person(Id.of(0), new SomeId("some_id"), new AnotherId("another_id"), "jack");

    // When
    var serResult = json.serialize(obj).get();
    var desResult = json.deserialize(jsonStr, Person.class).get();

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  private static ObjectMapper createMapper() {
    var subIdModule = JacksonUtils.subIdValueObjectsModule(SomeId.class, SomeId::new);
    var anotherIdModule = JacksonUtils.subIdValueObjectsModule(AnotherId.class, AnotherId::new);
    var om = JacksonUtils.json(subIdModule, anotherIdModule);
    JacksonUtils.prettyJson(om);
    JacksonUtils.addMixInPropertyFormat(om, Person.class);
    return om;
  }
}
