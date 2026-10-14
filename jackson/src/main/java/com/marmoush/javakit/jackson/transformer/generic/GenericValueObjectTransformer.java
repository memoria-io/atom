package com.marmoush.javakit.jackson.transformer.generic;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.function.Function;

public final class GenericValueObjectTransformer {

  private GenericValueObjectTransformer() {}

  public static class GenericValueObjectDeserializer<A, B extends A> extends StdDeserializer<B> {

    private final transient Function<String, B> constructor;

    public GenericValueObjectDeserializer(Class<B> vc, Function<String, B> constructor) {
      super(vc);
      this.constructor = constructor;
    }

    @Override
    public B deserialize(JsonParser p, DeserializationContext context) throws JacksonException {
      return constructor.apply(p.getString());
    }
  }

  public static class GenericValueObjectSerializer<A, B extends A> extends StdSerializer<B> {

    private final transient Function<B, String> valueExtractor;

    public GenericValueObjectSerializer(Class<B> vc, Function<B, String> valueExtractor) {
      super(vc);
      this.valueExtractor = valueExtractor;
    }

    @Override
    public void serialize(B value, JsonGenerator gen, SerializationContext context) throws JacksonException {
      gen.writeString(valueExtractor.apply(value));
    }
  }
}