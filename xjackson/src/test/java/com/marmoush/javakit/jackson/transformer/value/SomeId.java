package com.marmoush.javakit.jackson.transformer.value;

import com.marmoush.javakit.core.domain.ValueObject;

record SomeId(String value) implements ValueObject<String> {}
