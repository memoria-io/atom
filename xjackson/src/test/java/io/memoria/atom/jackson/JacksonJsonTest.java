package io.memoria.atom.jackson;

import io.memoria.atom.core.text.TextException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonJsonTest {
  @Test
  void serializeEngineer() {
    var yamlEngineerJson = Tests.json.serialize(Resources.BOB_ENGINEER);
    assert Resources.BOB_ENGINEER_JSON != null;
    assertEquals(Resources.BOB_ENGINEER_JSON, yamlEngineerJson);
  }

  @Test
  void serializeManager() {
    var engineerYaml = Tests.json.serialize(Resources.ANNIKA_MANAGER);
    assert Resources.ANNIKA_MANAGER_JSON != null;
    assertEquals(Resources.ANNIKA_MANAGER_JSON, engineerYaml);
  }

  @Test
  void serializeEmployees() {
    var employeesJson = Tests.json.serialize(Resources.EMPLOYEES);
    assert Resources.EMPLOYEES_JSON != null;
    assertEquals(Resources.EMPLOYEES_JSON, employeesJson);
  }

  @Test
  void deserializeDepartment() throws TextException {
    // Given
    var expectedDepartment = new Department(List.of(Resources.ANNIKA_MANAGER,
                                                    Resources.BOB_ENGINEER,
                                                    Resources.ALEX_ENGINEER));
    // When
    var actualDepartment = Tests.json.deserialize(Resources.DEPARTMENT_JSON, Department.class);
    // Then
    assertEquals(expectedDepartment, actualDepartment);

  }

  @Test
  void deserializeEngineer() throws TextException {
    // When
    var engineer = Tests.json.deserialize(Resources.BOB_ENGINEER_JSON, Engineer.class);
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void deserializeManager() throws TextException {
    // When
    var manager = Tests.json.deserialize(Resources.ANNIKA_MANAGER_JSON, Manager.class);
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
    assertEquals(List.of(Resources.BOB_ENGINEER, Resources.ALEX_ENGINEER), manager.team());
  }

  @Test
  void deserializeEmployees() throws TextException {
    // When
    var employees = Tests.json.deserialize(Resources.EMPLOYEES_JSON, Employee[].class);
    // Then
    Assertions.assertThat(Resources.EMPLOYEES).hasSameElementsAs(Arrays.stream(employees).toList());
  }
}
