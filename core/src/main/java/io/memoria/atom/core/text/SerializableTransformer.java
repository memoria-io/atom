package io.memoria.atom.core.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class SerializableTransformer implements TextTransformer {
  @Override
  @SuppressWarnings("unchecked")
  public <T> T deserialize(String str, Class<T> tClass) throws IOException, ClassNotFoundException {
    var b64 = Base64.getDecoder().decode(str);
    var is = new ByteArrayInputStream(b64);
    try (var in = new ObjectInputStream(is)) {
      return (T) in.readObject();
    }
  }

  @Override
  public <T> String serialize(T t) throws IOException {
    var os = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(os)) {
      out.writeObject(t);
    }
    return Base64.getEncoder().encodeToString(os.toByteArray());
  }
}