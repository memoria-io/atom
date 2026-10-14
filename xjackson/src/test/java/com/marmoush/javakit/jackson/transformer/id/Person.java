package com.marmoush.javakit.jackson.transformer.id;

import com.marmoush.javakit.core.id.Id;

public record Person(Id id, SomeId someId, AnotherId anotherId, String name) {}
