package io.memoria.atom.core.text;

public interface TextTransformer {
  <T> String serialize(T t) throws TextException;

  <T> T deserialize(String str, Class<T> tClass) throws TextException;
}
