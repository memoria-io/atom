package io.memoria.atom.jackson.transformer.value;

import io.memoria.atom.core.domain.ValueObject;

record SomeId(String value) implements ValueObject<String> {}
