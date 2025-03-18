package io.memoria.atom.jackson.transformer.value;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.memoria.atom.core.domain.ValueObject;

import java.io.IOException;
import java.util.function.Function;

public final class ValueObjectTransformer {

  private ValueObjectTransformer() {}

  public static class ValueObjectDeserializer<E, T extends ValueObject<E>> extends StdDeserializer<T> {

    private final transient Function<String, T> constructor;

    public ValueObjectDeserializer(Class<T> vc, Function<String, T> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
      var value = p.readValueAs(String.class);
      return constructor.apply(value);
    }
  }

  public static class ValueObjectSerializer<E, T extends ValueObject<E>> extends StdSerializer<T> {

    public ValueObjectSerializer(Class<T> vc) {
      super(vc);
    }

    @Override
    public void serialize(T t, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(t.value().toString());
    }
  }
}

