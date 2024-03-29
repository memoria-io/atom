package io.memoria.atom.text.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.memoria.atom.core.file.ConfigFileOps;
import io.memoria.atom.core.text.Json;
import io.memoria.atom.core.text.Yaml;
import io.memoria.atom.text.jackson.cases.company.Employee;
import io.memoria.atom.text.jackson.cases.company.Engineer;
import io.memoria.atom.text.jackson.cases.company.Manager;

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
    yaml = new YamlJackson(JacksonUtils.yaml());
  }

  private static ObjectMapper jacksonJsonMapper(boolean isPretty) {
    var jsonOM = (isPretty) ? JacksonUtils.prettyJson() : JacksonUtils.json();
    JacksonUtils.addMixInPropertyFormat(jsonOM, Employee.class);
    jsonOM.registerSubtypes(Manager.class, Engineer.class);
    return jsonOM;
  }
}
