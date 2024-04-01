package io.memoria.atom.core.text;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public final class Person implements Serializable {
  private final String name;
  private final int age;
  private final Location l;
  private final Integer i;

  public Person(String name, int age, Location l) {
    this(name, age, l, null);
  }

  public Person(String name, int age, Location l, Integer i) {
    this.name = name;
    this.age = age;
    this.l = l;
    this.i = i;
  }

  public String name() {return name;}

  public int age() {return age;}

  public Location l() {return l;}

  public Optional<Integer> i() {return Optional.ofNullable(i);}

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != this.getClass())
      return false;
    var that = (Person) obj;
    return Objects.equals(this.name, that.name)
           && this.age == that.age
           && Objects.equals(this.l, that.l)
           && Objects.equals(this.i, that.i);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age, l, i);
  }

  @Override
  public String toString() {
    return "Person[" + "name=" + name + ", " + "age=" + age + ", " + "l=" + l + ", " + "i=" + i + ']';
  }
}
