package io.memoria.atom.text.jackson.cases.company;

import io.memoria.atom.core.eventsourcing.CommandId;
import io.memoria.atom.core.id.Id;
import io.vavr.collection.List;

import java.time.LocalDate;

public record Engineer(Id id, CommandId commandId, String name, LocalDate birthday, List<String> tasks)
        implements Employee {}
