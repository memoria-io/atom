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

  public static class ValueObjectDeserializer<A, B extends A> extends StdDeserializer<B> {

    private final Function<String, B> constructor;

    public ValueObjectDeserializer(Class vc, Function<String, B> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public B deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
      var value = p.readValueAs(String.class);
      return constructor.apply(value);
    }

  }

  public static class ValueObjectSerializer<A, B extends A> extends StdSerializer<B> {

    private final Function<B, String> valueExtractor;

    public ValueObjectSerializer(Class<B> vc, Function<B, String> valueExtractor) {
      super(vc);
      this.valueExtractor = valueExtractor;
    }

    @Override
    public void serialize(B value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(valueExtractor.apply(value));
    }

  }
}

