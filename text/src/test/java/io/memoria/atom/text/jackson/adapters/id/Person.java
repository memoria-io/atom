package io.memoria.atom.text.jackson.adapters.id;

import io.memoria.atom.core.id.Id;

public record Person(Id id, SomeId someId, String name) {}
