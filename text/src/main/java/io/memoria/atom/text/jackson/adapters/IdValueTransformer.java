package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.memoria.atom.core.id.Id;

import java.io.IOException;
import java.util.function.Function;

public final class IdValueTransformer {

  private IdValueTransformer() {}

  public static class IdValueDeserializer<T extends Id> extends StdDeserializer<T> {

    private final Function<String, T> constructor;

    public IdValueDeserializer(Class vc, Function<String, T> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
      var value = p.readValueAs(String.class);
      return constructor.apply(value);
    }

  }

  public static class IdValueSerializer<T extends Id> extends StdSerializer<T> {

    public IdValueSerializer(Class<T> vc) {
      super(vc);
    }

    @Override
    public void serialize(T t, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(t.value());
    }
  }
}

