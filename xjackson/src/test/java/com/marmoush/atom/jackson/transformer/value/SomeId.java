package com.marmoush.atom.jackson.transformer.value;

import com.marmoush.atom.core.domain.ValueObject;

record SomeId(String value) implements ValueObject<String> {}
