package io.memoria.atom.text.jackson.adapters;

import io.memoria.atom.core.id.Id;

record Person(Id id, SomeId someId, String name) {}
