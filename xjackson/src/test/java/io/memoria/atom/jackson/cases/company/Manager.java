package io.memoria.atom.jackson.cases.company;

import java.util.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
