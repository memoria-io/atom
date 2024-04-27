package io.memoria.atom.jackson.transformer.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.text.TextException;
import io.memoria.atom.jackson.JacksonUtils;
import io.memoria.atom.jackson.JsonJackson;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericObjectTransformerTest {
  private static final JsonJackson json = new JsonJackson(createMapper());

  @Test
  void genericValueObjectDirectMapping() throws TextException {
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
  void genericValueObjectInsideAnother() throws TextException {
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
    var subIdModule = JacksonUtils.genericValueObjectsModule(SomeId.class, SomeId::new, SomeId::myValue);
    var om = JacksonUtils.defaultJson(subIdModule);
    JacksonUtils.prettyJson(om);
    JacksonUtils.addMixInPropertyFormat(om, Person.class);
    return om;
  }

}
