package io.memoria.atom.core.file;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class EnvVarsOps {

  public static Map<String, String> toMap(Properties props) {
    return Set.copyOf(props.keySet())
              .stream()
              .filter(String.class::isInstance)
              .map(k -> (String) k)
              .collect(Collectors.toMap(k -> k, props::getProperty));
  }

  public static Map<String, String> getSystemEnv() {
    var envVarsMap = toMap(System.getProperties());
    envVarsMap.putAll(System.getenv());
    return envVarsMap;
  }
}
