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
import java.util.ArrayList;
import java.util.List;

public class PrometheusRegistryBuilder {
  private final PrometheusMeterRegistry registry;
  private final List<Tag> tagList;

  public PrometheusRegistryBuilder(String appName, String version) {
    this.tagList = new ArrayList<>();
    this.tagList.add(Tag.of("APPLICATION_NAME", appName));
    this.tagList.add(Tag.of("APPLICATION_VERSION", version));
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
  }

  public PrometheusMeterRegistry build() {
    registry.config().commonTags(tagList);
    return registry;
  }

  public PrometheusRegistryBuilder withTag(Tag tag) {
    this.tagList.add(tag);
    return this;
  }

  public PrometheusRegistryBuilder withTags(List<Tag> tags) {
    this.tagList.addAll(tags);
    return this;
  }

  public PrometheusRegistryBuilder withDefaultMetrics(PrometheusMeterRegistry registry) {
    return withLog4j2Metrics(registry).withThreadMetrics(registry)
                                      .withGCMetrics(registry)
                                      .withMemoryMetrics(registry)
                                      .withDiskSpaceMetrics(registry)
                                      .withProcessorMetrics(registry)
                                      .withUptimeMetrics(registry);
  }

  @SuppressWarnings("resource")
  public PrometheusRegistryBuilder withLog4j2Metrics(MeterRegistry registry) {
    new Log4j2Metrics().bindTo(registry);
    return this;
  }

  public PrometheusRegistryBuilder withThreadMetrics(MeterRegistry registry) {
    new ClassLoaderMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
    return this;
  }

  @SuppressWarnings({"java:S2095", "resource"})
  // Do not change JvmGcMetrics to try-with-resources as the JvmGcMetrics will not be available after (auto-)closing.
  // See https://github.com/micrometer-metrics/micrometer/issues/1492
  public PrometheusRegistryBuilder withGCMetrics(MeterRegistry registry) {
    JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();
    jvmGcMetrics.bindTo(registry);
    Runtime.getRuntime().addShutdownHook(new Thread(jvmGcMetrics::close));
    return this;
  }

  public PrometheusRegistryBuilder withMemoryMetrics(MeterRegistry registry) {
    new JvmMemoryMetrics().bindTo(registry);
    return this;
  }

  public PrometheusRegistryBuilder withDiskSpaceMetrics(MeterRegistry registry) {
    new DiskSpaceMetrics(new File("/")).bindTo(registry);
    return this;
  }

  public PrometheusRegistryBuilder withProcessorMetrics(MeterRegistry registry) {
    new ProcessorMetrics().bindTo(registry);
    return this;
  }

  public PrometheusRegistryBuilder withUptimeMetrics(MeterRegistry registry) {
    new UptimeMetrics().bindTo(registry);
    return this;
  }
}