package io.memoria.atom.text.jackson.cases.company;



public record Manager(String name, List<Engineer> team) implements Employee {}
