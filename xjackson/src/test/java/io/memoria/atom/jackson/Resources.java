package io.memoria.atom.jackson;

import io.memoria.atom.core.file.FileOps;
import io.memoria.atom.core.id.Id;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Resources {
  // Json Resources
  public static final String JSON_LIST;
  public static final String BOB_ENGINEER_JSON;
  public static final String ANNIKA_MANAGER_JSON;
  public static final String DEPARTMENT_JSON;
  public static final String NAME_CREATED_JSON;
  // Yaml Resources
  public static final String BOB_ENGINEER_YAML;
  public static final String ANNIKA_MANAGER_YAML;
  public static final Engineer BOB_ENGINEER;
  public static final Engineer ALEX_ENGINEER;
  public static final Manager ANNIKA_MANAGER;

  static {
    try {
      // Json Resources
      JSON_LIST = FileOps.readResource("cases/company/json/List.json");
      BOB_ENGINEER_JSON = FileOps.readResource("cases/company/json/Engineer.json");
      ANNIKA_MANAGER_JSON = FileOps.readResource("cases/company/json/Manager.json");
      DEPARTMENT_JSON = FileOps.readResource("cases/company/json/Department.json");
      NAME_CREATED_JSON = FileOps.readResource("cases/company/json/NameCreated.json");
      BOB_ENGINEER_YAML = FileOps.readResource("cases/company/yaml/Engineer.yaml");
      ANNIKA_MANAGER_YAML = FileOps.readResource("cases/company/yaml/Manager.yaml");

      // Objects
      BOB_ENGINEER = new Engineer(Id.of(0), "bob", LocalDate.of(2000, 1, 1), List.of("fix issue 1", "Fix issue 2"));
      ALEX_ENGINEER = new Engineer(Id.of(1), "alex", LocalDate.of(2000, 1, 1), List.of("fix issue 3", "Fix issue 4"));
      ANNIKA_MANAGER = new Manager("Annika", List.of(BOB_ENGINEER, ALEX_ENGINEER));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
