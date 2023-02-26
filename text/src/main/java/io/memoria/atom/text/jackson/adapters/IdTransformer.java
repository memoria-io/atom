package io.memoria.atom.text.jackson.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import io.memoria.atom.core.id.Id;

import java.io.IOException;

public final class IdTransformer {

  private IdTransformer() {}

  public static class IdDeserializer extends JsonDeserializer<Id> {
    @Override
    public Id deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Id.of(p.readValueAs(String.class));
    }
  }

  public static class IdSerializer extends JsonSerializer<Id> {
    @Override
    public void serialize(Id id, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(id.value());
    }
  }
}