package com.marmoush.atom.jackson;

import com.marmoush.atom.core.id.Id;

import java.time.LocalDate;
import java.util.List;

public record Engineer(Id id, String name, LocalDate birthday, List<String> tasks) implements Employee {}
