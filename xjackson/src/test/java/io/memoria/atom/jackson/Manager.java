package io.memoria.atom.jackson;

import java.util.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
