package io.memoria.atom.text.jackson.cases.company;

import io.memoria.atom.text.jackson.Resources;
import io.memoria.atom.text.jackson.TestDeps;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonJacksonTest {
  @Test
  void serializeEngineer() {
    var yamlEngineer = TestDeps.json.serialize(Resources.BOB_ENGINEER).get();
    assert Resources.BOB_ENGINEER_JSON != null;
    assertEquals(Resources.BOB_ENGINEER_JSON, yamlEngineer);
  }

  @Test
  void serializeManager() {
    var yamlEngineer = TestDeps.json.serialize(Resources.ANNIKA_MANAGER).get();
    assert Resources.ANNIKA_MANAGER_JSON != null;
    assertEquals(Resources.ANNIKA_MANAGER_JSON, yamlEngineer);
  }

  @Test
  void serializeByHOF() {
    // Given
    var serializer = TestDeps.json.serialize();
    // When
    var managers = List.range(0, 100).map(i -> Resources.ANNIKA_MANAGER).map(serializer).map(Try::get);
    // Then
    managers.forEach(manager -> assertEquals(Resources.ANNIKA_MANAGER_JSON, manager));
  }

  @Test
  void deserializeDepartment() {
    // Given
    var expectedDepartment = new Department(List.of(Resources.ANNIKA_MANAGER,
                                                    Resources.BOB_ENGINEER,
                                                    Resources.ALEX_ENGINEER));
    // When
    var actualDepartment = TestDeps.json.deserialize(Resources.DEPARTMENT_JSON, Department.class).get();
    // Then
    assertEquals(expectedDepartment, actualDepartment);

  }

  @Test
  void deserializeEngineer() {
    // When
    var engineer = TestDeps.json.deserialize(Resources.BOB_ENGINEER_JSON, Engineer.class).get();
    // Then
    assertEquals(Resources.BOB_ENGINEER, engineer);
  }

  @Test
  void deserializeManager() {
    // When
    var manager = TestDeps.json.deserialize(Resources.ANNIKA_MANAGER_JSON, Manager.class).get();
    // Then
    assertEquals(Resources.ANNIKA_MANAGER, manager);
    assertEquals(List.of(Resources.BOB_ENGINEER, Resources.ALEX_ENGINEER), manager.team());
  }

  @Test
  void deserializeByHOF() {
    // Given
    var toManager = TestDeps.json.deserialize(Manager.class);
    // When
    var managers = List.range(0, 100).map(i -> Resources.ANNIKA_MANAGER_JSON).map(toManager).map(Try::get);
    // Then
    managers.forEach(manager -> {
      assertEquals(Resources.ANNIKA_MANAGER, manager);
      assertEquals(List.of(Resources.BOB_ENGINEER, Resources.ALEX_ENGINEER), manager.team());
    });
  }
}
