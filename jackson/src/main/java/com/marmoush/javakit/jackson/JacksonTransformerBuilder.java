package com.marmoush.javakit.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.marmoush.javakit.core.domain.ValueObject;
import com.marmoush.javakit.core.id.Id;
import com.marmoush.javakit.core.id.Ids;
import com.marmoush.javakit.core.text.TextTransformer;
import com.marmoush.javakit.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectDeserializer;
import com.marmoush.javakit.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectSerializer;
import com.marmoush.javakit.jackson.transformer.id.IdTransformer.IdDeserializer;
import com.marmoush.javakit.jackson.transformer.id.IdTransformer.IdSerializer;
import com.marmoush.javakit.jackson.transformer.value.ValueObjectTransformer.ValueObjectDeserializer;
import com.marmoush.javakit.jackson.transformer.value.ValueObjectTransformer.ValueObjectSerializer;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.core.util.Separators;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.function.Function;

import static tools.jackson.core.util.Separators.Spacing.NONE;
import static tools.jackson.dataformat.yaml.YAMLWriteFeature.INDENT_ARRAYS_WITH_INDICATOR;

public final class JacksonTransformerBuilder {
  private final MapperBuilder<?, ?> builder;

  public JacksonTransformerBuilder(MapperBuilder<?, ?> builder) {
    this.builder = Objects.requireNonNull(builder);
  }

  public static JacksonTransformerBuilder json() {
    return new JacksonTransformerBuilder(JsonMapper.builder());
  }

  public static JacksonTransformerBuilder yaml() {
    return new JacksonTransformerBuilder(YAMLMapper.builder().enable(INDENT_ARRAYS_WITH_INDICATOR));
  }

  public TextTransformer asTextTransformer() {
    return new JacksonTransformer(builder.build());
  }

  /** Basic settings: withDefaultDateFormat().withDurationAsTimestamp().withIdModule() */
  public JacksonTransformerBuilder withDefaults() {
    return withDefaultDateFormat().withDurationAsTimestamp().withIdModule();
  }

  public JacksonTransformerBuilder withPrettyFormat() {
    Separators separators = Separators.createDefaultInstance().withObjectNameValueSpacing(NONE);
    var printer = new DefaultPrettyPrinter(separators);
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    builder.enable(SerializationFeature.INDENT_OUTPUT);
    builder.defaultPrettyPrinter(printer);
    return this;
  }

  public JacksonTransformerBuilder withDefaultDateFormat() {
    builder.defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    return this;
  }

  public JacksonTransformerBuilder withDurationAsTimestamp() {
    builder.disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    return this;
  }

  public JacksonTransformerBuilder withIdModule() {
    var module = new SimpleModule().addSerializer(Id.class, new IdSerializer<>(Id.class))
                                   .addDeserializer(Id.class, new IdDeserializer<>(Id.class, Ids::of));
    builder.addModule(module);
    return this;
  }

  /**
   * Maps inheriting classes' simple names, written as "As.PROPERTY" with property name "$type", to this baseClass
   * argument.
   *
   * <p>Subclasses must be in separate files from their baseClass, otherwise the JVM returns
   * "BaseClass$ChildClass" style names.
   */
  public JacksonTransformerBuilder withMixInPropertyFormat(Class<?>... baseClass) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      builder.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return this;
  }

  /** Same as above but written as "As.WRAPPER_OBJECT". */
  public JacksonTransformerBuilder withMixInWrapperObjectFormat(Class<?>... baseClass) {
    @JsonTypeInfo(include = As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      builder.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return this;
  }

  public <T extends Id> JacksonTransformerBuilder withSubIdValueObjectsModule(Class<T> tClass,
                                                                              Function<String, T> fromString) {
    var module = new SimpleModule().addDeserializer(tClass, new IdDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new IdSerializer<>(tClass));
    return withModules(module);
  }

  public <E, T extends ValueObject<E>> JacksonTransformerBuilder withValueObjectsModule(Class<T> tClass,
                                                                                        Function<String, T> fromString) {
    var module = new SimpleModule().addDeserializer(tClass, new ValueObjectDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new ValueObjectSerializer<>(tClass));
    return withModules(module);
  }

  public <A, B extends A> JacksonTransformerBuilder withGenericValueObjectsModule(Class<B> tClass,
                                                                                  Function<String, B> fromString,
                                                                                  Function<B, String> toString) {
    var module = new SimpleModule().addDeserializer(tClass, new GenericValueObjectDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new GenericValueObjectSerializer<>(tClass, toString));
    return withModules(module);
  }

  public JacksonTransformerBuilder withModules(JacksonModule... modules) {
    for (JacksonModule module : modules) {
      builder.addModule(module);
    }
    return this;
  }

  public JacksonTransformerBuilder withSubtypes(Class<?>... subtypes) {
    builder.registerSubtypes(subtypes);
    return this;
  }

  /** Builds a new immutable mapper from the current configuration on every call. */
  public ObjectMapper objectMapper() {
    return builder.build();
  }
}