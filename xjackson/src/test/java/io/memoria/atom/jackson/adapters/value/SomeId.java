package io.memoria.atom.jackson.adapters.value;

import io.memoria.atom.core.domain.ValueObject;

public record SomeId(String value) implements ValueObject<String> {}
