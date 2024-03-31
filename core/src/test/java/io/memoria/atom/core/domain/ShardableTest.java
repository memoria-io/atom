package io.memoria.atom.core.domain;

import io.memoria.atom.core.id.Id;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShardableTest {
  private static final int totalShards = 1000;
  private static final int minPartitions = 25;
  private static final int maxPartitions = 75;

  @ParameterizedTest
  @MethodSource("totalPartitions")
  void uuidShardsShouldBeNormal(int totalPartitions) {
    // Given
    var shards = createShards(_ -> Id.of(UUID.randomUUID()));

    // When
    var outliers = validate(shards, totalPartitions);

    // Then
    var maxOutliers = (int) (totalPartitions * 0.1);
    System.out.println(STR. "Max outliers:\{ maxOutliers }, Found outliers:\{ outliers }" );
    Assertions.assertThat(outliers).size().isLessThanOrEqualTo(maxOutliers);
  }

  @ParameterizedTest
  @MethodSource("totalPartitions")
  void longShardsShouldBeNormal(int totalPartitions) {
    // Given
    var shards = createShards(Id::of);

    // When
    var outliers = validate(shards, totalPartitions);

    // Then
    var maxOutliers = (int) (totalPartitions * 0.1);
    System.out.println(STR. "Max outliers:\{ maxOutliers }, Found outliers:\{ outliers }" );
    Assertions.assertThat(outliers).size().isLessThanOrEqualTo(maxOutliers);
  }

  private List<Double> validate(List<Shard> shards, int totalPartitions) {
    var partitionSizeList = IntStream.range(0, totalPartitions)
                                     .mapToObj(partition -> partitionSize(shards, partition, totalPartitions))
                                     .map(Long::doubleValue)
                                     .toList();

    // Then
    double mean = mean(partitionSizeList);
    double sigma = standardDeviation(mean, partitionSizeList);
    var high = mean + (3 * sigma);
    var low = mean + (-3 * sigma);

    return partitionSizeList.stream().filter(i -> i > high || i < low).toList();
  }

  private static List<Shard> createShards(Function<Integer, Id> idGen) {
    return IntStream.range(0, totalShards).mapToObj(idGen::apply).map(Shard::new).toList();
  }

  public static double standardDeviation(double mean, List<Double> list) {
    double standardDeviation = 0.0;
    for (double num : list) {
      standardDeviation += Math.pow(num - mean, 2);
    }
    return Math.sqrt(standardDeviation / (list.size() - 1));
  }

  private static double mean(List<Double> list) {
    double sum = 0.0;
    for (double i : list) {
      sum += i;
    }
    return sum / list.size();
  }

  public static Stream<Arguments> totalPartitions() {
    return IntStream.range(minPartitions, maxPartitions).mapToObj(Arguments::of);
  }

  private static long partitionSize(List<Shard> shards, int partition, int totalPartitions) {
    return shards.stream().filter(sh -> sh.isInPartition(partition, totalPartitions)).count();
  }

  private record Shard(Id shardKey) implements Shardable {}
}
