package com.marmoush.javakit.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmoush.javakit.core.text.TextException;
import com.marmoush.javakit.core.text.TextRuntimeException;
import com.marmoush.javakit.core.text.TextTransformer;

record JacksonTransformer(ObjectMapper mapper) implements TextTransformer {

  @Override
  public <T> String serialize(T t) {
    try {
      return mapper.writeValueAsString(t);
    } catch (JsonProcessingException e) {
      throw TextRuntimeException.of(e);
    }
  }

  @Override
  public <T> T deserialize(String str, Class<T> tClass) throws TextException {
    try {
      return mapper.readValue(str, tClass);
    } catch (JsonProcessingException e) {
      throw TextException.of(e);
    }
  }
}
