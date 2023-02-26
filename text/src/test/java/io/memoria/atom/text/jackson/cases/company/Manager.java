package io.memoria.atom.text.jackson.cases.company;

import io.vavr.collection.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
