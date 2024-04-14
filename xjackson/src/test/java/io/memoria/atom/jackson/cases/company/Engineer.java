package io.memoria.atom.jackson.cases.company;

import io.memoria.atom.core.id.Id;

import java.time.LocalDate;
import java.util.List;

public record Engineer(Id id, String name, LocalDate birthday, List<String> tasks) implements Employee {}
