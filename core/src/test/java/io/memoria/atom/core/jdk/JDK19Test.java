package io.memoria.atom.core.jdk;

import org.junit.jupiter.api.Test;

class JDK19Test {
  @Test
  void testRecordPattern() {
    Shape sq = new Square(new Point(3, 1), new Point(1, 3));
    Shape cir = new Circle(new Point(0, 0), 3);
    assert area(sq) == 4;
    assert area(cir) > 9;
  }

  static double area(Shape r) {
    return switch (r) {
      case Circle(Point p,int radius)when r instanceof Circle -> Math.PI * radius;
      case Square(Point(int x1,int y1),Point(int x2,int y2))when r instanceof Square ->
              Math.abs(x2 - x1) * Math.abs(y2 - y1);
      case null -> throw new NullPointerException();
      default -> 0;
    };
  }

  private sealed interface Shape {}

  private record Circle(Point center, int radius) implements Shape {}

  private record Point(int x, int y) implements Shape {}

  private record Square(Point x, Point y) implements Shape {}
}
