package io.memoria.atom.core.domain;

import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.Ids;
import io.memoria.atom.core.math.MathOps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Using Standard deviation to see if there's max of 10% outliers or sharding distribution
 *
 * @see <a href="https://study.com/skill/learn/determining-outliers-using-standard-deviation-explanation.html">Determine
 * outliers using standard deviation</a>
 */
public class PartitionedTest {
  private static final int totalShards = 1000;
  private static final int minPartitions = 25;
  private static final int maxPartitions = 75;

  @ParameterizedTest
  @MethodSource("totalPartitions")
  void uuidShardsShouldBeNormal(int totalPartitions) {
    // Given
    var shards = createShards(_ -> Ids.of(UUID.randomUUID()));
    var partitionSizeList = partitionSizeList(shards, totalPartitions).stream().map(Long::doubleValue).toList();
    int maxOutliers = getMaxOutliers(totalPartitions);

    // When
    var outliers = MathOps.findStdDevOutliers(partitionSizeList);

    // Then
    Assertions.assertThat(outliers).size().isLessThanOrEqualTo(maxOutliers);
  }

  @ParameterizedTest
  @MethodSource("totalPartitions")
  void longShardsShouldBeNormal(int totalPartitions) {
    // Given
    var shards = createShards(Ids::of);
    var partitionSizeList = partitionSizeList(shards, totalPartitions).stream().map(Long::doubleValue).toList();

    // When
    var outliers = MathOps.findStdDevOutliers(partitionSizeList);

    // Then
    var maxOutliers = getMaxOutliers(totalPartitions);
    Assertions.assertThat(outliers).size().isLessThanOrEqualTo(maxOutliers);
  }

  public static Stream<Arguments> totalPartitions() {
    return IntStream.range(minPartitions, maxPartitions).mapToObj(Arguments::of);
  }

  private static int getMaxOutliers(int totalPartitions) {
    return (int) (totalPartitions * 0.1);
  }

  private static List<Partitioned> createShards(Function<Integer, Id> idGen) {
    return IntStream.range(0, totalShards).mapToObj(idGen::apply).map(Shard::new).map(s -> (Partitioned) s).toList();
  }

  private static List<Long> partitionSizeList(List<Partitioned> shards, int totalPartitions) {
    return IntStream.range(0, totalPartitions)
                    .mapToObj(partition -> isInPartition(shards, partition, totalPartitions))
                    .toList();
  }

  private static long isInPartition(List<Partitioned> shards, int partition, int totalPartitions) {
    return shards.stream().filter(sh -> sh.isInPartition(partition, totalPartitions)).count();
  }

  private record Shard(Id pKey) implements Partitioned {}
}
