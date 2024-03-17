package io.memoria.atom.core.text;

import java.io.IOException;

public interface TextTransformer {
  <T> String serialize(T t) throws IOException;

  <T> T deserialize(String str, Class<T> tClass) throws IOException, ClassNotFoundException;
}
