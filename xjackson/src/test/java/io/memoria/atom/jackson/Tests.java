package io.memoria.atom.jackson;

import io.memoria.atom.core.file.ConfigFileOps;
import io.memoria.atom.core.text.TextTransformer;

public class Tests {
  public static final ConfigFileOps CONFIG_FILE_OPS;
  public static final TextTransformer json;
  public static final TextTransformer compactJson;
  public static final TextTransformer yaml;

  static {
    CONFIG_FILE_OPS = new ConfigFileOps("include:", false);

    json = jsonBuilder().withPrettyFormat().asTextTransformer();
    compactJson = jsonBuilder().asTextTransformer();
    yaml = JacksonTransformerBuilder.yaml().withDefaults().asTextTransformer();
  }

  private static JacksonTransformerBuilder jsonBuilder() {
    return JacksonTransformerBuilder.json()
                                    .withDefaults()
                                    .withMixInPropertyFormat(Employee.class)
                                    .withSubtypes(Manager.class, Engineer.class);
  }
}
