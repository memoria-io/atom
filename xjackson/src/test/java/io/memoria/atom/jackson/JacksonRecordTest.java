package io.memoria.atom.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.memoria.atom.core.text.TextException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.memoria.atom.jackson.TestDeps.json;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A Copy from <a
 * href="https://github.com/FasterXML/jackson-databind/blob/977fbb3dcfa7ad7fda4f45f7f0c1afa0916b702e/src/test/java/com/fasterxml/jackson/databind/BaseMapTest.java">BaseMapTest</a>
 */
class JacksonRecordTest {

  private static JsonMapper jsonMapper;

  public String asYaml(String jsonString) throws IOException {
    // parse JSON
    JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
    // save it as YAML
    return new YAMLMapper().writeValueAsString(jsonNodeTree);
  }

  @Test
  void testDeserializeRecordWithConstructor() throws IOException {
    RecordWithConstructor value = jsonMapper.readValue("{\"id\":123,\"name\":\"Bob\"}", RecordWithConstructor.class);

    assertEquals(new RecordWithConstructor(123, "Bob"), value);
  }

  @Test
  void testDeserializeSimpleRecord() throws IOException {
    SimpleRecord value = jsonMapper.readValue("{\"id\":123,\"name\":\"Bob\"}", SimpleRecord.class);

    assertEquals(new SimpleRecord(123, "Bob"), value);
  }

  @Test
  void testSerializeJsonIgnoreRecord() throws JsonProcessingException {
    JsonIgnoreRecord record = new JsonIgnoreRecord(123, "Bob");

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"id\":123}", json);
  }

  @Test
  void testSerializeRecordOfRecord() throws JsonProcessingException {
    RecordOfRecord record = new RecordOfRecord(new SimpleRecord(123, "Bob"));

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"record\":{\"id\":123,\"name\":\"Bob\"}}", json);
  }

  @Test
  void testSerializeSimpleRecord() throws JsonProcessingException {
    SimpleRecord record = new SimpleRecord(123, "Bob");

    String json = jsonMapper.writeValueAsString(record);

    assertEquals("{\"id\":123,\"name\":\"Bob\"}", json);
  }

  @Test
  void toList() throws TextException {
    // When
    var list = json.deserialize(Resources.JSON_LIST, String[].class);
    // Then
    assert list != null;
    assertEquals(List.of("mercedes", "chevy", "porsche"), List.of(list));
  }

  @BeforeAll
  static void setUp() {
    jsonMapper = new JsonMapper();
  }

  record JsonIgnoreRecord(int id, @JsonIgnore String name) {}

  record RecordOfRecord(SimpleRecord record) {}

  record RecordWithConstructor(int id, String name) {}

  record SimpleRecord(int id, String name) {}
}
