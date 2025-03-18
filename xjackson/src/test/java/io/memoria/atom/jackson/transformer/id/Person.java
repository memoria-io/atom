package io.memoria.atom.jackson.transformer.id;

import io.memoria.atom.core.id.Id;

public record Person(Id id, SomeId someId, AnotherId anotherId, String name) {}
