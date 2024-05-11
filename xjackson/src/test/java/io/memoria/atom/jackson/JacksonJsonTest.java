package io.memoria.atom.jackson;

import io.memoria.atom.core.text.TextException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonJsonTest {
  @Test
  void serializeEngineer() {
    var yamlEngineer = Tests.json.serialize(Resources.BOB_ENGINEER);
    assert Resources.BOB_ENGINEER_JSON != null;
    assertEquals(Resources.BOB_ENGINEER_JSON, yamlEngineer);
  }

  @Test
  void serializeManager() {
    var yamlEngineer = Tests.json.serialize(Resources.ANNIKA_MANAGER);
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
}
