package io.memoria.atom.core.text;

import java.io.Serializable;

public record Person(String name, int age, Location l) implements Serializable {}
