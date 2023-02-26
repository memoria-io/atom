package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.function.Function;

public final class ValueObjectTransformer {

  private ValueObjectTransformer() {}

  public static class ValueObjectDeserializer<T> extends StdDeserializer<T> {

    private final Function<String, T> constructor;

    public ValueObjectDeserializer(Class vc, Function<String, T> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
      var value = p.readValueAs(String.class);
      return constructor.apply(value);
    }

  }

  public static class ValueObjectSerializer<T> extends StdSerializer<T> {

    private final Function<T, String> valueExtractor;

    public ValueObjectSerializer(Class<T> vc, Function<T, String> valueExtractor) {
      super(vc);
      this.valueExtractor = valueExtractor;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(valueExtractor.apply(value));
    }

  }
}

