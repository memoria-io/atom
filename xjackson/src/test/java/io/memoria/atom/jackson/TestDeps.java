package io.memoria.atom.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.file.ConfigFileOps;
import io.memoria.atom.core.text.Json;
import io.memoria.atom.core.text.Yaml;

public class TestDeps {
  public static final ConfigFileOps CONFIG_FILE_OPS;
  public static final Json json;
  public static final Json compactJson;
  public static final Yaml yaml;

  static {
    // File utils
    CONFIG_FILE_OPS = new ConfigFileOps("include:", false);
    // Json
    json = new JsonJackson(jacksonJsonMapper(true));
    compactJson = new JsonJackson(jacksonJsonMapper(false));
    // Yaml
    yaml = new YamlJackson(JacksonUtils.defaultYaml());
  }

  private static ObjectMapper jacksonJsonMapper(boolean isPretty) {
    var jsonOM = JacksonUtils.defaultJson();
    if (isPretty) {
      JacksonUtils.prettyJson(jsonOM);
    }
    JacksonUtils.addMixInPropertyFormat(jsonOM, Employee.class);
    jsonOM.registerSubtypes(Manager.class, Engineer.class);
    return jsonOM;
  }
}
