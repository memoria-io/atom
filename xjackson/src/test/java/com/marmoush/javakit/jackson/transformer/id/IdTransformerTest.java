package com.marmoush.javakit.jackson.transformer.id;

import com.marmoush.javakit.core.id.Ids;
import com.marmoush.javakit.core.text.TextException;
import com.marmoush.javakit.core.text.TextTransformer;
import com.marmoush.javakit.jackson.JacksonTransformerBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class IdTransformerTest {
  private static final TextTransformer json = create();

  @Test
  void idSubTypesDirectMapping() throws TextException {
    // Given
    var jsonStr = "\"some_id\"";
    var obj = SomeId.of(Ids.of("some_id"));

    // When
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, SomeId.class);

    // Then
    Assertions.assertThat(serResult).isEqualTo(jsonStr);
    Assertions.assertThat(desResult).isEqualTo(obj);
  }

  @Test
  void idSubTypesInObject() throws TextException {
    // Given
    var jsonStr = """
            {
              "$type":"Person",
              "id":"0",
              "someId":"some_id",
              "anotherId":"another_id",
              "name":"jack"
            }""";
    var obj = new Person(Ids.of(0), new SomeId("some_id"), new AnotherId("another_id"), "jack");

    // When
    var serResult = json.serialize(obj);
    var desResult = json.deserialize(jsonStr, Person.class);

    // Then
    Assertions.assertThat(serResult).isEqualTo(jsonStr);
    Assertions.assertThat(desResult).isEqualTo(obj);
  }

  private static TextTransformer create() {
    return JacksonTransformerBuilder.json()
                                    .withDefaults()
                                    .withPrettyFormat()
                                    .withSubtypes(SomeId.class, AnotherId.class)
                                    .withMixInPropertyFormat(Person.class)
                                    .asTextTransformer();
  }
}
