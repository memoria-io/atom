package io.memoria.atom.text.jackson.cases.company;

import java.util.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
