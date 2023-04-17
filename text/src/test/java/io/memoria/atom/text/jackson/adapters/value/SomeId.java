package io.memoria.atom.text.jackson.adapters.value;

import io.memoria.atom.core.ValueObject;

public record SomeId(String value) implements ValueObject<String> {}
