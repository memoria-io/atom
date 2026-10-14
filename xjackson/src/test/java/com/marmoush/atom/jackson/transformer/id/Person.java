package com.marmoush.atom.jackson.transformer.id;

import com.marmoush.atom.core.id.Id;

public record Person(Id id, SomeId someId, AnotherId anotherId, String name) {}
