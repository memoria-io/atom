package io.memoria.atom.core.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ValidationsTest {
  @ParameterizedTest
  @MethodSource("validEmails")
  void checkValidEmails(String email) {
    Assertions.assertThat(Validations.isValidEmail(email)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidEmails")
  void checkInvalidEmails(String email) {
    Assertions.assertThat(Validations.isValidEmail(email)).isFalse();
  }

  public static Stream<Arguments> validEmails() {
    return Stream.of("email@example.com",
                     "firstname.lastname@example.com",
                     "email@subdomain.example.com",
                     "firstname+lastname@example.com",
                     "1234567890@example.com",
                     "email@example-one.com",
                     "_______@example.com",
                     "email@example.name",
                     "email@example.museum",
                     "email@example.co.jp",
                     "firstname-lastname@example.com").map(Arguments::of);

  }

  public static Stream<Arguments> invalidEmails() {
    return Stream.of("plainaddress",
                     "\"email\"@example.com",
                     "email@123.123.123.123",
                     "email@[123.123.123.123]",
                     "#@%^%#$@#$@#.com",
                     "@example.com",
                     "Joe Smith <email@example.com>",
                     "email.example.com",
                     "email@example@example.com",
                     ".email@example.com",
                     "email.@example.com",
                     "email..email@example.com",
                     "あいうえお@example.com",
                     "email@example.com (Joe Smith)",
                     "email@example",
                     "email@111.222.333.44444",
                     "email@example..com",
                     "Abc..123@example.com",
                     "”(),:;<>[\\]@example.com",
                     "just”not”right@example.com",
                     "much.”more unusual”@example.com",
                     "very.unusual.”@”.unusual.com@example.com",
                     "very.”(),:;<>[]”.VERY.”very@\\\\ \"very”.unusual@strange.example.com",
                     "this\\ is\"really\"not\\allowed@example.com").map(Arguments::of);
  }

}
