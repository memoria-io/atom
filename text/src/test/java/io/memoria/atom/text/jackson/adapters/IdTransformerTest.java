package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
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
  void idSubTypesDirectMapping() throws JsonProcessingException {
    // Given
    var jsonStr = "\"some_id\"";
    var obj = SomeId.of(Id.of("some_id"));

    // When
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, SomeId.class);

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  @Test
  void idSubTypesInObject() throws JsonProcessingException {
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
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, Person.class);

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  private static ObjectMapper createMapper() {
    var om = JacksonUtils.json();
    om.registerSubtypes(SomeId.class, AnotherId.class);
    JacksonUtils.prettyJson(om);
    JacksonUtils.addMixInPropertyFormat(om, Person.class);
    return om;
  }
}
