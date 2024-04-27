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
import io.memoria.atom.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectDeserializer;
import io.memoria.atom.jackson.transformer.generic.GenericValueObjectTransformer.GenericValueObjectSerializer;
import io.memoria.atom.jackson.transformer.id.IdTransformer.IdDeserializer;
import io.memoria.atom.jackson.transformer.id.IdTransformer.IdSerializer;
import io.memoria.atom.jackson.transformer.value.ValueObjectTransformer.ValueObjectDeserializer;
import io.memoria.atom.jackson.transformer.value.ValueObjectTransformer.ValueObjectSerializer;

import java.text.SimpleDateFormat;
import java.util.function.Function;

import static com.fasterxml.jackson.core.util.Separators.Spacing.NONE;

public class JacksonUtils {
  private JacksonUtils() {}

  /**
   * Main json ObjectMapper
   */
  public static ObjectMapper defaultJson(Module... modules) {
    ObjectMapper om = JsonMapper.builder().build();
    setDateFormat(om);
    addJ8Modules(om);
    om.registerModule(getIdModule());
    for (Module module : modules) {
      om.registerModule(module);
    }
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return om;
  }

  public static void prettyJson(ObjectMapper om) {
    Separators separators = Separators.createDefaultInstance().withObjectFieldValueSpacing(NONE);
    var printer = new DefaultPrettyPrinter(separators);
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    om.enable(SerializationFeature.INDENT_OUTPUT);
    om.setDefaultPrettyPrinter(printer);
  }

  /**
   * Main Yaml ObjectMapper
   */
  public static ObjectMapper defaultYaml(Module... modules) {
    var yfb = new YAMLFactoryBuilder(YAMLFactory.builder().build());
    yfb.configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true);
    var om = new ObjectMapper(yfb.build());
    setDateFormat(om);
    addJ8Modules(om);
    om.registerModule(getIdModule());
    for (Module module : modules) {
      om.registerModule(module);
    }
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return om;
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
  public static void addMixInPropertyFormat(ObjectMapper om, Class<?>... baseClass) {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      om.addMixIn(cls, WrapperObjectByClassName.class);
    }
  }

  /**
   * Maps inheriting classes simple names written with format "As.WRAPPER_OBJECT" to this baseClass argument
   * <p>
   * note for this to work properly subclasses have to be in separate files from their baseClass otherwise Jvm will
   * return "BaseClass$ChildClass" kind of naming
   *
   * @param baseClass base classes
   */
  public static void addMixInWrapperObjectFormat(ObjectMapper om, Class<?>... baseClass) {
    @JsonTypeInfo(include = As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    class WrapperObjectByClassName {}
    for (Class<?> cls : baseClass) {
      om.addMixIn(cls, WrapperObjectByClassName.class);
    }
  }

  public static void setDateFormat(ObjectMapper om) {
    om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd" + "'T'" + "HH:mm:ss"));
  }

  public static void addJ8Modules(ObjectMapper om) {
    om.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
  }

  public static <T extends Id> SimpleModule subIdValueObjectsModule(Class<T> tClass, Function<String, T> fromString) {
    var atomModule = new SimpleModule();
    atomModule.addDeserializer(tClass, new IdDeserializer<>(tClass, fromString));
    atomModule.addSerializer(tClass, new IdSerializer<>(tClass));
    return atomModule;
  }

  public static <E, T extends ValueObject<E>> SimpleModule valueObjectsModule(Class<T> tClass,
                                                                              Function<String, T> fromString) {
    var atomModule = new SimpleModule();
    atomModule.addDeserializer(tClass, new ValueObjectDeserializer<>(tClass, fromString));
    atomModule.addSerializer(tClass, new ValueObjectSerializer<>(tClass));
    return atomModule;
  }

  public static <A, B extends A> SimpleModule genericValueObjectsModule(Class<B> tClass,
                                                                        Function<String, B> fromString,
                                                                        Function<B, String> toString) {
    var atomModule = new SimpleModule();
    atomModule.addDeserializer(tClass, new GenericValueObjectDeserializer<>(tClass, fromString));
    atomModule.addSerializer(tClass, new GenericValueObjectSerializer<>(tClass, toString));
    return atomModule;
  }

  private static SimpleModule getIdModule() {
    return new SimpleModule().addSerializer(Id.class, new IdSerializer<>(Id.class))
                             .addDeserializer(Id.class, new IdDeserializer<>(Id.class, Id::of));
  }
}
