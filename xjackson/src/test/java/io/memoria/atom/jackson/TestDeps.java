package io.memoria.atom.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.file.ConfigFileOps;
import io.memoria.atom.core.text.TextTransformer;

public class TestDeps {
  public static final ConfigFileOps CONFIG_FILE_OPS;
  public static final TextTransformer json;
  public static final TextTransformer compactJson;
  public static final TextTransformer yaml;

  static {
    // File utils
    CONFIG_FILE_OPS = new ConfigFileOps("include:", false);
    // Json
    json = XJackson.jsonTransformer(jacksonJsonMapper(true));
    compactJson = XJackson.jsonTransformer(jacksonJsonMapper(false));
    // Yaml
    yaml = XJackson.yamlTransformer(XJackson.yamlObjectMapper());
  }

  private static ObjectMapper jacksonJsonMapper(boolean isPretty) {
    var jsonOM = XJackson.jsonObjectMapper();
    if (isPretty) {
      XJackson.pretty(jsonOM);
    }
    XJackson.addMixInPropertyFormat(jsonOM, Employee.class);
    jsonOM.registerSubtypes(Manager.class, Engineer.class);
    return jsonOM;
  }
}
