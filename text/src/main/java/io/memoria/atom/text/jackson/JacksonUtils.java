package io.memoria.atom.text.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
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
import io.memoria.atom.core.ValueObject;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.text.jackson.adapters.GenericValueObjectTransformer.GenericValueObjectDeserializer;
import io.memoria.atom.text.jackson.adapters.GenericValueObjectTransformer.GenericValueObjectSerializer;
import io.memoria.atom.text.jackson.adapters.IdTransformer.IdDeserializer;
import io.memoria.atom.text.jackson.adapters.IdTransformer.IdSerializer;
import io.memoria.atom.text.jackson.adapters.ValueObjectTransformer.ValueObjectDeserializer;
import io.memoria.atom.text.jackson.adapters.ValueObjectTransformer.ValueObjectSerializer;
import io.vavr.collection.Map;
import io.vavr.jackson.datatype.VavrModule;

import java.text.SimpleDateFormat;
import java.util.function.Function;

public class JacksonUtils {
  private JacksonUtils() {}

  /**
   * Main json ObjectMapper
   */
  public static ObjectMapper json(Module... modules) {
    ObjectMapper om = JsonMapper.builder().build();
    setDateFormat(om);
    addJ8Modules(om);
    addVavrModule(om);
    om.registerModule(getIdModule());
    for (Module module : modules) {
      om.registerModule(module);
    }
    om.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    return om;
  }

  private static SimpleModule getIdModule() {
    return new SimpleModule().addSerializer(Id.class, new IdSerializer<>(Id.class))
                             .addDeserializer(Id.class, new IdDeserializer<>(Id.class, Id::of));
  }

  /**
   * Main Yaml ObjectMapper
   */
  public static ObjectMapper yaml(Module... modules) {
    var yfb = new YAMLFactoryBuilder(YAMLFactory.builder().build());
    yfb.configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true);
    var om = new ObjectMapper(yfb.build());
    setDateFormat(om);
    addJ8Modules(om);
    addVavrModule(om);
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

  public static void addVavrModule(ObjectMapper om) {
    om.registerModule(new VavrModule());
  }

  public static ObjectMapper prettyJson() {
    ObjectMapper json = json();
    prettyJson(json);
    return json;
  }

  public static void prettyJson(ObjectMapper om) {
    var printer = new DefaultPrettyPrinter().withoutSpacesInObjectEntries();
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    om.enable(SerializationFeature.INDENT_OUTPUT);
    om.setDefaultPrettyPrinter(printer);
  }

  public static <T extends Id> SimpleModule subIdValueObjectsModule(Map<Class<T>, Function<String, T>> eClasses) {
    var atomModule = new SimpleModule();
    eClasses.forEach(tup -> atomModule.addDeserializer(tup._1, new IdDeserializer<T>(tup._1, tup._2)));
    eClasses.forEach(tup -> atomModule.addSerializer(tup._1, new IdSerializer<>(tup._1.asSubclass(Id.class))));
    return atomModule;
  }

  public static <E, T extends ValueObject<E>> SimpleModule valueObjectsModule(Map<Class<T>, Function<String, T>> eClasses) {
    var atomModule = new SimpleModule();
    eClasses.forEach(tup -> atomModule.addDeserializer(tup._1, new ValueObjectDeserializer<>(tup._1, tup._2)));
    eClasses.forEach(tup -> atomModule.addSerializer(tup._1,
                                                     new ValueObjectSerializer<>(tup._1.asSubclass(ValueObject.class))));
    return atomModule;
  }

//  public static <A, B extends A> SimpleModule genericValueObjectsModule(Map<Class<B>, Function<String, B>> deserializers,
//                                                                        Map<Class<B>, Function<B, String>> serializers,
//                                                                        Class<A> aClass) {
//    var atomModule = new SimpleModule();
//    deserializers.forEach(tup -> atomModule.addDeserializer(tup._1,
//                                                            new GenericValueObjectDeserializer<A, B>(tup._1, tup._2)));
//    serializers.forEach(tup -> atomModule.addSerializer(tup._1,
//                                                        new GenericValueObjectSerializer<>(tup._1.asSubclass(aClass),
//                                                                                           tup._2)));
//    return atomModule;
//  }
}
