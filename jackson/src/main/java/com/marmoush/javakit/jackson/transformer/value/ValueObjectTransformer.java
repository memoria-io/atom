package com.marmoush.javakit.jackson.transformer.value;

import com.marmoush.javakit.core.domain.ValueObject;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

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
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
      return constructor.apply(p.getString());
    }
  }

  public static class ValueObjectSerializer<E, T extends ValueObject<E>> extends StdSerializer<T> {

    public ValueObjectSerializer(Class<T> vc) {
      super(vc);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
      gen.writeString(value.value().toString());
    }
  }
}