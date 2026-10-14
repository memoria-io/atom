package com.marmoush.javakit.jackson;

import java.util.List;

public record Manager(String name, List<Engineer> team) implements Employee {}
