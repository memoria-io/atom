package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;

public record Topic(String topic, int partition) {
  public static final String SPLIT_TOKEN = "_";
  public static final String SUBJECT_EXT = ".subject";

  public Topic {
    validateName(topic, partition);
  }

  public String streamName() {
    return "%s%s%d".formatted(topic, SPLIT_TOKEN, partition);
  }

  public String subjectName() {
    return streamName() + SUBJECT_EXT;
  }

  private void validateName(String name, int partition) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name can't be null or empty string");
    }
    if (partition < 0) {
      throw new IllegalArgumentException("Partition can't be less than 0");
    }
  }

  public static Topic create(String topic, int partition) {
    return new Topic(topic, partition);
  }

  public static Topic fromMsg(ESMsg ESMsg) {
    return new Topic(ESMsg.topic(), ESMsg.partition());
  }

  public static Topic fromSubject(String subject) {
    var idx = subject.indexOf(SUBJECT_EXT);
    var s = subject.substring(0, idx).split(SPLIT_TOKEN);
    var topic = s[0];
    var partition = Integer.parseInt(s[1]);
    return Topic.create(topic, partition);
  }
}
