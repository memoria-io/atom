package io.memoria.atom.core.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class SerializableTransformer implements TextTransformer {
  @Override
  public <T> String serialize(T t) {
    var os = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(os)) {
      out.writeObject(t);
    } catch (IOException e) {
      throw TextRuntimeException.of(e);
    }
    return Base64.getEncoder().encodeToString(os.toByteArray());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T deserialize(String str, Class<T> tClass) throws TextException {
    var b64 = Base64.getDecoder().decode(str);
    var is = new ByteArrayInputStream(b64);
    try (var in = new ObjectInputStream(is)) {
      return (T) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw TextException.of(e);
    }
  }
}