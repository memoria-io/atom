package io.memoria.atom.text.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.text.Yaml;

public record YamlJackson(ObjectMapper mapper) implements Yaml {

  @Override
  public <T> T deserialize(String str, Class<T> tClass) throws JsonProcessingException {
    return mapper.readValue(str, tClass);
  }

  @Override
  public <T> String serialize(T t) throws JsonProcessingException {
    return mapper.writeValueAsString(t);
  }
}
