package io.memoria.atom.core.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigFileOps {
  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  public static final String VAR_PREFIX = "${";
  public static final String VAR_POSTFIX = "}";
  private final String nestingPrefix;
  private final boolean enableVariableInterpolation;
  private final Map<String, String> envVars;

  public ConfigFileOps(String nestingPrefix, boolean enableVariableInterpolation) {
    this.nestingPrefix = nestingPrefix;
    this.enableVariableInterpolation = enableVariableInterpolation;
    this.envVars = getEnvVars(enableVariableInterpolation);
  }

  /**
   * @param enableVariableInterpolation when true, any line which contains ${ENV_VALUE:-defaultValue} will be resolved
   *                                    from system environment then from java systemProperties
   */
  public ConfigFileOps(boolean enableVariableInterpolation) {
    this.nestingPrefix = null;
    this.enableVariableInterpolation = enableVariableInterpolation;
    this.envVars = getEnvVars(enableVariableInterpolation);
  }

  /**
   * if the path parameter doesn't start with "/" it's considered a file under the resources directory
   */
  public String read(String path) throws IOException {
    return expand(path, null).stream().reduce("", JOIN_LINES);
  }

  Map<String, String> getEnvVars(boolean enableVariableInterpolation) {
    final Map<String, String> envVars;
    if (enableVariableInterpolation) {
      envVars = toMap(System.getProperties());
      envVars.putAll(System.getenv());
    } else {
      envVars = Map.of();
    }
    return envVars;
  }

  Map<String, String> toMap(Properties props) {
    return Set.copyOf(props.keySet())
              .stream()
              .filter(String.class::isInstance)
              .map(k -> (String) k)
              .collect(Collectors.toMap(k -> k, props::getProperty));
  }

  List<String> expand(String path, String line) throws IOException {
    if (line == null) {
      List<String> result = new ArrayList<>();
      for (String l : ResourceFileOps.readResourceOrFile(path)) {
        var expanded = expand(path, l).stream().map(this::resolveLineExpression).toList();
        result.addAll(expanded);
      }
      return result;
    }
    if (nestingPrefix != null && line.trim().startsWith(nestingPrefix)) {
      var subFilePath = line.substring(nestingPrefix.length()).trim();
      var relativePath = parentPath(path) + subFilePath;
      return expand(relativePath, null);
    } else {
      return List.of(line);
    }
  }

  String removeBraces(String line) {
    StringBuilder stringBuilder = new StringBuilder(line);
    var openingIdx = stringBuilder.indexOf(VAR_PREFIX);
    stringBuilder.replace(openingIdx, openingIdx + 2, "");
    var closingIdx = stringBuilder.lastIndexOf(VAR_POSTFIX);
    stringBuilder.replace(closingIdx, closingIdx + 1, "");
    return stringBuilder.toString().trim();
  }

  Optional<String> resolveExpression(String expression) {
    expression = removeBraces(expression);
    var split = expression.split(":-");
    if (split.length == 1) {
      var key = split[0];
      return Optional.ofNullable(this.envVars.get(key));
    }
    if (split.length == 2) {
      var key = split[0];
      var defaultValue = split[1];
      return Optional.ofNullable(this.envVars.get(key)).or(() -> Optional.of(defaultValue));
    }
    return Optional.empty();
  }

  String resolveLineExpression(String line) {
    if (this.enableVariableInterpolation) {
      var p = Pattern.compile("\\$\\{[\\sa-zA-Z_0-9]+(:-)?.+}");//NOSONAR
      var f = p.matcher(line);
      var matches = new java.util.HashMap<String, String>();
      while (f.find()) {
        var match = line.substring(f.start(), f.end());
        matches.put(match, resolveExpression(match).orElse(match));
      }
      for (Entry<String, String> entry : matches.entrySet()) {
        line = line.replace(entry.getKey(), entry.getValue());
      }
    }
    return line;
  }

  private static String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", ""); //NOSONAR
  }
}
