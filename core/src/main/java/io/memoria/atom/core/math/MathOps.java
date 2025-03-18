package io.memoria.atom.core.math;

import java.util.List;

public class MathOps {
  private MathOps() {}

  public static double standardDeviation(List<Double> list, double mean) {
    double standardDeviation = 0.0;
    for (double num : list) {
      standardDeviation += Math.pow(num - mean, 2);
    }
    return Math.sqrt(standardDeviation / (list.size() - 1));
  }

  public static double mean(List<Double> list) {
    double sum = 0.0;
    for (double i : list) {
      sum += i;
    }
    return sum / list.size();
  }

  public static List<Double> findStdDevOutliers(List<Double> data) {
    double mean = mean(data);
    double sigma = standardDeviation(data, mean);
    var high = mean + (3 * sigma);
    var low = mean + (-3 * sigma);

    return data.stream().filter(i -> i > high || i < low).toList();
  }
}
