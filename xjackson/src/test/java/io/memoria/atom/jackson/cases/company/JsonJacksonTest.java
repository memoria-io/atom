package io.memoria.atom.jackson.cases.company;

import io.memoria.atom.core.text.TextException;
import io.memoria.atom.jackson.Resources;
import io.memoria.atom.jackson.TestDeps;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {
  @Test
  void serializeEngineer() throws TextException {
    var yamlEngineer = TestDeps.json.serialize(Resources.BOB_ENGINEER);
    assert Resources.BOB_ENGINEER_JSON != null;
    assertEquals(Resources.BOB_ENGINEER_JSON, yamlEngineer);
  }

  @Test
  void serializeManager() throws TextException {
    var yamlEngineer = TestDeps.json.serialize(Resources.ANNIKA_MANAGER);
    assert Resources.ANNIKA_MANAGER_JSON != null;
    assertEquals(Resources.ANNIKA_MANAGER_JSON, yamlEngineer);
  }

  @Test
  void deserializeDepartment() throws TextException {
    // Given
    var expectedDepartment = new Department(List.of(Resources.ANNIKA_MANAGER,
                                                    Resources.BOB_ENGINEER,
                                                    Resources.ALEX_ENGINEER));
    // When
    var actualDepartment = TestDeps.json.deserialize(Resources.DEPARTMENT_JSON, Department.class);
    // Then
    assertEquals(expectedDepartment, actualDepartment);

  }

  @Test
  void deserializeEngineer() throws TextException {
    // When
    var engineer = TestDeps.json.deserialize(Resources.BOB_ENGINEER_JSON, Engineer.class);
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void deserializeManager() throws TextException {
    // When
    var manager = TestDeps.json.deserialize(Resources.ANNIKA_MANAGER_JSON, Manager.class);
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
    assertEquals(List.of(Resources.BOB_ENGINEER, Resources.ALEX_ENGINEER), manager.team());
  }
}
