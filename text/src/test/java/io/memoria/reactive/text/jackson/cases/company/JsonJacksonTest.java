package io.memoria.reactive.text.jackson.cases.company;

import io.memoria.reactive.text.jackson.Resources;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static io.memoria.reactive.text.jackson.TestDeps.prettyJson;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(Resources.ANNIKA_MANAGER,
                                                    Resources.BOB_ENGINEER,
                                                    Resources.ALEX_ENGINEER));
    // When
    var actualDepartment = prettyJson.deserialize(Resources.DEPARTMENT_JSON, Department.class).get();
    // Then
    assertEquals(expectedDepartment, actualDepartment);

  }

  @Test
  void deserializeEngineer() {
    // When
    var engineer = prettyJson.deserialize(Resources.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void deserializeManager() {
    // When
    var manager = prettyJson.deserialize(Resources.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
    assertEquals(List.of(Resources.BOB_ENGINEER, Resources.ALEX_ENGINEER), manager.team());
  }
}
