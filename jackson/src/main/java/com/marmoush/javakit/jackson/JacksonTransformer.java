package com.marmoush.javakit.jackson;

import com.marmoush.javakit.core.text.TextException;
import com.marmoush.javakit.core.text.TextRuntimeException;
import com.marmoush.javakit.core.text.TextTransformer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

record JacksonTransformer(ObjectMapper mapper) implements TextTransformer {

  @Override
  public <T> String serialize(T t) {
    try {
      return mapper.writeValueAsString(t);
    } catch (JacksonException e) {
      throw TextRuntimeException.of(e);
    }
  }

  @Override
  public <T> T deserialize(String str, Class<T> tClass) throws TextException {
    try {
      return mapper.readValue(str, tClass);
    } catch (JacksonException e) {
      throw TextException.of(e);
    }
  }
}