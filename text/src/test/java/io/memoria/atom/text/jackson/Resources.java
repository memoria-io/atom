package io.memoria.atom.text.jackson;

import io.memoria.atom.core.file.ResourceFileOps;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.text.jackson.cases.company.Engineer;
import io.memoria.atom.text.jackson.cases.company.Manager;


import java.time.LocalDate;

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
    // Json Resources
    JSON_LIST = ResourceFileOps.read("cases/company/json/List.json").get();
    BOB_ENGINEER_JSON = ResourceFileOps.read("cases/company/json/Engineer.json").get();
    ANNIKA_MANAGER_JSON = ResourceFileOps.read("cases/company/json/Manager.json").get();
    DEPARTMENT_JSON = ResourceFileOps.read("cases/company/json/Department.json").get();
    NAME_CREATED_JSON = ResourceFileOps.read("cases/company/json/NameCreated.json").get();

    BOB_ENGINEER_YAML = ResourceFileOps.read("cases/company/yaml/Engineer.yaml").get();
    ANNIKA_MANAGER_YAML = ResourceFileOps.read("cases/company/yaml/Manager.yaml").get();
    // Objects
    BOB_ENGINEER = new Engineer(Id.of(0), "bob", LocalDate.of(2000, 1, 1), List.of("fix issue 1", "Fix issue 2"));
    ALEX_ENGINEER = new Engineer(Id.of(1), "alex", LocalDate.of(2000, 1, 1), List.of("fix issue 3", "Fix issue 4"));
    ANNIKA_MANAGER = new Manager("Annika", List.of(BOB_ENGINEER, ALEX_ENGINEER));
  }

  private Resources() {}
}
