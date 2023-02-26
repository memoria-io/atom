package io.memoria.atom.text.jackson.cases.company;

import io.vavr.collection.List;

public record Department(List<Employee> employees) {}
