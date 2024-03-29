package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.text.jackson.JacksonUtils;
import io.memoria.atom.text.jackson.JsonJackson;
import io.memoria.atom.text.jackson.adapters.value.Person;
import io.memoria.atom.text.jackson.adapters.value.SomeId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValueObjectTransformerTest {
  private static final JsonJackson json = new JsonJackson(createMapper());

  @Test
  void valueObjectDirectMapping() throws JsonProcessingException {
    // Given
    var jsonStr = "\"some_id\"";
    var obj = new SomeId("some_id");

    // When
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, SomeId.class);

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  @Test
  void valueObjectInsideAnother() throws JsonProcessingException {
    // Given
    var jsonStr = """
            {
              "$type":"Person",
              "someId":"some_id",
              "name":"jack"
            }""";
    var obj = new Person(new SomeId("some_id"), "jack");

    // When
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, Person.class);

    // Then
    assertThat(serResult).isEqualTo(jsonStr);
    assertThat(desResult).isEqualTo(obj);
  }

  private static ObjectMapper createMapper() {
    var subIdModule = JacksonUtils.valueObjectsModule(SomeId.class, SomeId::new);
    var om = JacksonUtils.json(subIdModule);
    JacksonUtils.prettyJson(om);
    JacksonUtils.addMixInPropertyFormat(om, Person.class);
    return om;
  }
}
