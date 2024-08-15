package io.memoria.atom.core.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ConfigFile {
  public static final BinaryOperator<String> JOIN_LINES = (a, b) -> a + System.lineSeparator() + b;
  public static final String VAR_PREFIX = "${";
  public static final String VAR_POSTFIX = "}";
  public static final String FILE_VAR_PREFIX = "file://";
  public static final String DEFAULT_NESTING_PREFIX = "#include:";

  private final String filePath;
  private final String nestingPrefix;
  private final boolean enableVariableInterpolation;
  private final Map<String, String> envVars;
  private final List<IOException> exceptions;

  public ConfigFile(String filePath) {
    this(filePath, DEFAULT_NESTING_PREFIX, true);
  }

  /**
   * @param enableVariableInterpolation when true, any line which contains ${ENV_VALUE:-defaultValue} will be resolved
   *                                    from system environment then from java systemProperties
   */
  public ConfigFile(String filePath, boolean enableVariableInterpolation) {
    this(filePath, DEFAULT_NESTING_PREFIX, enableVariableInterpolation);
  }

  public ConfigFile(String filePath, String nestingPrefix, boolean enableVariableInterpolation) {
    this.filePath = filePath;
    this.nestingPrefix = nestingPrefix;
    this.enableVariableInterpolation = enableVariableInterpolation;
    this.envVars = (enableVariableInterpolation) ? EnvVarsOps.getSystemEnv() : Map.of();
    this.exceptions = new ArrayList<>();
  }

  /**
   * if the path parameter doesn't start with "/" it's considered a file under the resources directory
   */
  public String read() throws IOException {
    return readLines().reduce(JOIN_LINES).orElse("");
  }

  public Stream<String> readLines() throws IOException {
    var result = readLines(filePath, false).flatMap(this::handleNesting).map(this::handleExpression);
    if (exceptions.isEmpty()) {
      return result;
    } else {
      throw exceptions.getFirst();
    }
  }

  Stream<String> handleNesting(String line) {
    if (nestingPrefix != null && line.trim().startsWith(nestingPrefix)) {
      var path = line.substring(nestingPrefix.length()).trim();
      return readLines(path, true);
    } else {
      return Stream.of(line);
    }
  }

  String handleExpression(String line) {
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

  Optional<String> resolveExpression(String expression) {
    expression = removeBraces(expression);
    var split = expression.split(":-");
    var result = Optional.<String>empty();
    if (split.length > 1) {
      result = readValue(split[0]);
      if (split.length == 2) {
        result = result.or(() -> Optional.of(split[1]));
      }
    }
    return result;
  }

  String removeBraces(String line) {
    StringBuilder stringBuilder = new StringBuilder(line);
    var openingIdx = stringBuilder.indexOf(VAR_PREFIX);
    stringBuilder.replace(openingIdx, openingIdx + 2, "");
    var closingIdx = stringBuilder.lastIndexOf(VAR_POSTFIX);
    stringBuilder.replace(closingIdx, closingIdx + 1, "");
    return stringBuilder.toString().trim();
  }

  Optional<String> readValue(String expression) {
    if (expression.startsWith(FILE_VAR_PREFIX)) {
      var path = expression.replace(FILE_VAR_PREFIX, "");
      return readLines(path, true).reduce(JOIN_LINES);
    } else {
      return Optional.ofNullable(this.envVars.get(expression));
    }
  }

  Stream<String> readLines(String path, boolean relative) {
    try {
      if (path.startsWith("/")) {
        return Files.lines(Path.of(path));
      } else {
        var relativePath = (relative) ? parentPath(this.filePath) + path : path;
        return FileOps.readResourceLines(relativePath);
      }
    } catch (IOException e) {
      this.exceptions.add(e);
      return Stream.of();
    }
  }

  private static String parentPath(String filePath) {
    return filePath.replaceFirst("[^/]+$", ""); //NOSONAR
  }
}
