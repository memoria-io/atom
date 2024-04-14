package io.memoria.atom.jackson.adapters.id;

import io.memoria.atom.core.id.Id;

public record Person(Id id, SomeId someId, AnotherId anotherId, String name) {}
