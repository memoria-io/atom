package io.memoria.atom.core.stack;

import java.io.Serializable;

public record StackItem(StackId stackId, int itemIndex, String value) implements Serializable {}
