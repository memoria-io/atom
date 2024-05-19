package io.memoria.atom.web.observability;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
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
  public static final String APPLICATION_NAME_TAG = "APPLICATION_NAME";
  public static final String APPLICATION_VERSION_TAG = "APPLICATION_VERSION";

  private final List<Tag> tagList;
  private final List<MeterBinder> meters;
  private final PrometheusMeterRegistry registry;

  public PrometheusRegistryBuilder() {
    this(PrometheusConfig.DEFAULT);
  }

  public PrometheusRegistryBuilder(PrometheusConfig prometheusConfig) {
    this.tagList = new ArrayList<>();
    this.meters = new ArrayList<>();
    this.registry = new PrometheusMeterRegistry(prometheusConfig);
  }

  public PrometheusMeterRegistry build() {
    registry.config().commonTags(tagList);
    this.meters.forEach(m -> m.bindTo(registry));
    return registry;
  }

  public PrometheusRegistryBuilder withAppNameTag(String applicationName) {
    this.tagList.add(Tag.of(APPLICATION_NAME_TAG, applicationName));
    return this;
  }

  public PrometheusRegistryBuilder withAppVersionTag(String applicationVersion) {
    this.tagList.add(Tag.of(APPLICATION_VERSION_TAG, applicationVersion));
    return this;
  }

  public PrometheusRegistryBuilder withTag(Tag tag) {
    this.tagList.add(tag);
    return this;
  }

  public PrometheusRegistryBuilder withTags(List<Tag> tags) {
    this.tagList.addAll(tags);
    return this;
  }

  public PrometheusRegistryBuilder withDefaultMetrics() {
    return withLog4j2Metrics().withThreadMetrics()
                              .withGCMetrics()
                              .withMemoryMetrics()
                              .withDiskSpaceMetrics()
                              .withProcessorMetrics()
                              .withUptimeMetrics();
  }

  public PrometheusRegistryBuilder withLog4j2Metrics() {
    this.meters.add(new Log4j2Metrics());
    return this;
  }

  public PrometheusRegistryBuilder withThreadMetrics() {
    this.meters.add(new ClassLoaderMetrics());
    this.meters.add(new JvmThreadMetrics());
    return this;
  }

  @SuppressWarnings({"java:S2095"})
  // Do not change JvmGcMetrics to try-with-resources as the JvmGcMetrics will not be available after (auto-)closing.
  // See https://github.com/micrometer-metrics/micrometer/issues/1492
  public PrometheusRegistryBuilder withGCMetrics() {
    JvmGcMetrics jvmGcMetrics = new JvmGcMetrics();
    this.meters.add(jvmGcMetrics);
    Runtime.getRuntime().addShutdownHook(new Thread(jvmGcMetrics::close));
    return this;
  }

  public PrometheusRegistryBuilder withMemoryMetrics() {
    this.meters.add(new JvmMemoryMetrics());
    return this;
  }

  public PrometheusRegistryBuilder withDiskSpaceMetrics() {
    this.meters.add(new DiskSpaceMetrics(new File("/")));
    return this;
  }

  public PrometheusRegistryBuilder withProcessorMetrics() {
    this.meters.add(new ProcessorMetrics());
    return this;
  }

  public PrometheusRegistryBuilder withUptimeMetrics() {
    this.meters.add(new UptimeMetrics());
    return this;
  }
}