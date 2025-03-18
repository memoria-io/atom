package io.memoria.atom.core.utils;

/**
 * This class provides validations utilities
 *
 * @see <a href="https://owasp.org/www-community/OWASP_Validation_Regex_Repository">OWASP Regex Repository </a>
 */
@SuppressWarnings("java:S5998")
public class Validations {
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

  private Validations() {}

  public static boolean isValidEmail(String email) {
    var splits = email.split("@");
    if (splits.length == 2) {
      return splits[0].length() <= 64 && splits[1].length() <= 255 && email.matches(EMAIL_REGEX);
    } else {
      return false;
    }
  }
}
