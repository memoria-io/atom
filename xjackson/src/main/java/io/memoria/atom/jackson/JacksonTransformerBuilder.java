package io.memoria.atom.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.memoria.atom.core.domain.ValueObject;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.Ids;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectDeserializer;
import io.memoria.atom.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectSerializer;
import io.memoria.atom.jackson.transformer.id.IdTransformer.IdDeserializer;
import io.memoria.atom.jackson.transformer.id.IdTransformer.IdSerializer;
import io.memoria.atom.jackson.transformer.value.ValueObjectTransformer.ValueObjectDeserializer;
import io.memoria.atom.jackson.transformer.value.ValueObjectTransformer.ValueObjectSerializer;

import java.text.SimpleDateFormat;
import java.util.function.Function;

import static com.fasterxml.jackson.core.util.Separators.Spacing.NONE;

public record JacksonTransformerBuilder(ObjectMapper objectMapper) {

  public static JacksonTransformerBuilder json() {
    return new JacksonTransformerBuilder(JsonMapper.builder().build());
  }

  public static JacksonTransformerBuilder yaml() {
    var yfb = new YAMLFactoryBuilder(YAMLFactory.builder().build());
    yfb.configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true);
    return new JacksonTransformerBuilder(new ObjectMapper(yfb.build()));
  }

  public TextTransformer asTextTransformer() {
    return new JacksonTransformer(objectMapper);
  }

  /**
   * @return JacksonObjectMapper with basic settings
   * withDefaultDateFormat().withDurationAsTimestamp().withJ8Modules().withIdModule()
   */
  public JacksonTransformerBuilder withDefaults() {
    return withDefaultDateFormat().withDurationAsTimestamp().withJ8Modules().withIdModule();
  }

  public JacksonTransformerBuilder withPrettyFormat() {
    Separators separators = Separators.createDefaultInstance().withObjectFieldValueSpacing(NONE);
    var printer = new DefaultPrettyPrinter(separators);
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.setDefaultPrettyPrinter(printer);
    return this;
  }

  public JacksonTransformerBuilder withDefaultDateFormat() {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd" + "'T'" + "HH:mm:ss"));
    return this;
  }

  public JacksonTransformerBuilder withDurationAsTimestamp() {
    objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return this;
  }

  public JacksonTransformerBuilder withJ8Modules() {
    withModules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule());
    return this;
  }

  public JacksonTransformerBuilder withIdModule() {
    var module = new SimpleModule().addSerializer(Id.class, new IdSerializer<>(Id.class))
                                   .addDeserializer(Id.class, new IdDeserializer<>(Id.class, Ids::of));
    objectMapper.registerModule(module);
    return this;
  }

  /**
   * Maps inheriting classes simple names written with format "As.PROPERTY" and property name is "@type" to this
   * baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   */
  public JacksonTransformerBuilder withMixInPropertyFormat(Class<?>... baseClass) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      objectMapper.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return this;
  }

  /**
   * Maps inheriting classes simple names written with format "As.WRAPPER_OBJECT" to this baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   */
  public JacksonTransformerBuilder withMixInWrapperObjectFormat(Class<?>... baseClass) {
    @JsonTypeInfo(include = As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      objectMapper.addMixIn(cls, WrapperObjectByClassName.class);
    }
    return this;
  }

  public <T extends Id> JacksonTransformerBuilder withSubIdValueObjectsModule(Class<T> tClass,
                                                                              Function<String, T> fromString) {
    var module = new SimpleModule().addDeserializer(tClass, new IdDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new IdSerializer<>(tClass));
    withModules(module);
    return this;
  }

  public <E, T extends ValueObject<E>> JacksonTransformerBuilder withValueObjectsModule(Class<T> tClass,
                                                                                        Function<String, T> fromString) {
    var module = new SimpleModule().addDeserializer(tClass, new ValueObjectDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new ValueObjectSerializer<>(tClass));
    withModules(module);
    return this;
  }

  public <A, B extends A> JacksonTransformerBuilder withGenericValueObjectsModule(Class<B> tClass,
                                                                                  Function<String, B> fromString,
                                                                                  Function<B, String> toString) {
    var module = new SimpleModule().addDeserializer(tClass, new GenericValueObjectDeserializer<>(tClass, fromString))
                                   .addSerializer(tClass, new GenericValueObjectSerializer<>(tClass, toString));
    withModules(module);
    return this;
  }

  public JacksonTransformerBuilder withModules(Module... modules) {
    for (Module module : modules) {
      objectMapper.registerModule(module);
    }
    return this;
  }

  public JacksonTransformerBuilder withSubtypes(Class<?>... subtypes) {
    objectMapper.registerSubtypes(subtypes);
    return this;
  }
}
