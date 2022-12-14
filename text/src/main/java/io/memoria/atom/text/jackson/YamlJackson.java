package io.memoria.atom.text.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.text.TextException;
import io.memoria.atom.core.text.Yaml;
import io.vavr.API;
import io.vavr.Predicates;
import io.vavr.control.Try;

public record YamlJackson(ObjectMapper mapper) implements Yaml {

  @Override
  @SuppressWarnings("unchecked")
  public <T> Try<T> deserialize(String str, Class<T> tClass) {
    return Try.of(() -> mapper.readValue(str, tClass))
              .mapFailure(API.Case(API.$(Predicates.instanceOf(JacksonException.class)),
                                   e -> new TextException(e.getMessage())));
  }

  @Override
  public <T> Try<String> serialize(T t) {
    return Try.of(() -> mapper.writeValueAsString(t));
  }
}
