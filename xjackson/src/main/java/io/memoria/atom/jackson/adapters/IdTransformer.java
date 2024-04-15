package io.memoria.atom.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.memoria.atom.core.id.Id;

import java.io.IOException;
import java.util.function.Function;

public final class IdTransformer {

  private IdTransformer() {}

  public static class IdDeserializer<T extends Id> extends StdDeserializer<T> {

    private final transient Function<String, T> constructor;

    public IdDeserializer(Class<T> vc, Function<String, T> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
      var value = p.readValueAs(String.class);
      return constructor.apply(value);
    }
  }

  public static class IdSerializer<T extends Id> extends StdSerializer<T> {

    public IdSerializer(Class<T> vc) {
      super(vc);
    }

    @Override
    public void serialize(T t, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(t.value());
    }
  }
}

