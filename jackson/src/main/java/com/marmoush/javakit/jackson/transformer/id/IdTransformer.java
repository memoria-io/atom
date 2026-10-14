package com.marmoush.javakit.jackson.transformer.id;

import com.marmoush.javakit.core.id.Id;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

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
    public T deserialize(JsonParser p, DeserializationContext context) throws JacksonException {
      return constructor.apply(p.getString());
    }
  }

  public static class IdSerializer<T extends Id> extends StdSerializer<T> {

    public IdSerializer(Class<T> vc) {
      super(vc);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializationContext context) throws JacksonException {
      gen.writeString(value.value());
    }
  }
}