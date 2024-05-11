package io.memoria.atom.jackson.transformer.generic;

import io.memoria.atom.core.text.TextException;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.jackson.JacksonTransformerBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericObjectTransformerTest {
  private static final TextTransformer json = create();

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

  private static TextTransformer create() {
    return JacksonTransformerBuilder.json()
                                    .withDefaults()
                                    .withPrettyFormat()
                                    .withGenericValueObjectsModule(SomeId.class, SomeId::new, SomeId::myValue)
                                    .withMixInPropertyFormat(Person.class)
                                    .asTextTransformer();
  }
}
