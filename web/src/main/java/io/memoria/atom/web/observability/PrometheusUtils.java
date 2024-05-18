package io.memoria.atom.web.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.Log4j2Metrics;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.File;
import java.util.List;

public class PrometheusUtils {
  private PrometheusUtils() {}

  public static PrometheusMeterRegistry createRegistry(String appName, String version, List<Tag> tagList) {
    tagList.add(Tag.of("APPLICATION_NAME", appName));
    tagList.add(Tag.of("APPLICATION_VERSION", version));
    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    registry.config().commonTags(tagList);
    return registry;
  }

  public static void includeDefaultMetrics(PrometheusMeterRegistry registry) {
    includeLog4j2Metrics(registry);
    includeThreadMetrics(registry);
    includeGCMetrics(registry);
    includeMemoryMetrics(registry);
    includeDiskSpaceMetrics(registry);
    includeProcessorMetrics(registry);
    includeUptimeMetrics(registry);
  }

  @SuppressWarnings("resource")
  public static void includeLog4j2Metrics(MeterRegistry registry) {
    new Log4j2Metrics().bindTo(registry);
  }

  public static void includeThreadMetrics(MeterRegistry registry) {
    new ClassLoaderMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
  }

  @SuppressWarnings({"java:S2095", "resource"})
  // Do not change JvmGcMetrics to try-with-resources as the JvmGcMetrics will not be available after (auto-)closing.
  // See https://github.com/micrometer-metrics/micrometer/issues/1492
  public static void includeGCMetrics(MeterRegistry registry) {
    JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();
    jvmGcMetrics.bindTo(registry);
    Runtime.getRuntime().addShutdownHook(new Thread(jvmGcMetrics::close));
  }

  public static void includeMemoryMetrics(MeterRegistry registry) {
    new JvmMemoryMetrics().bindTo(registry);
  }

  public static void includeDiskSpaceMetrics(MeterRegistry registry) {
    new DiskSpaceMetrics(new File("/")).bindTo(registry);
  }

  public static void includeProcessorMetrics(MeterRegistry registry) {
    new ProcessorMetrics().bindTo(registry);
  }

  public static void includeUptimeMetrics(MeterRegistry registry) {
    new UptimeMetrics().bindTo(registry);
  }
}