package io.memoria.atom.core.utils;

/**
 * <p>This class provides validations utilities</p>
 *
 * <p>
 *
 * @see <a href="https://owasp.org/www-community/OWASP_Validation_Regex_Repository">OWASP Regex Repository </a>
 * </p>
 */
public class Validations {
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

  private Validations() {}

  public static boolean isValidEmail(String email) {
    return email.matches(EMAIL_REGEX);
  }
}
