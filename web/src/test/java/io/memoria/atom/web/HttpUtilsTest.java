package io.memoria.atom.web;

import io.memoria.atom.web.auth.BasicCredential;
import io.memoria.atom.web.auth.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.NoSuchElementException;

class HttpUtilsTest {

  @Test
  void basicSuccess() {
    // Given
    var header = "Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());

    // When
    var credential = HttpUtils.credential(header);

    // Then
    Assertions.assertThat(credential).isEqualTo(new BasicCredential("bob", "password"));
  }

  @Test
  void basicExtraSpacesSuccess() {
    // Given
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes()) + "   ";

    // When
    var credential = HttpUtils.credential(header);

    // Then
    Assertions.assertThat(credential).isEqualTo(new BasicCredential("bob", "password"));
  }

  @Test
  void basicExtraSpacesFail() {
    // Given
    var header = "Basic  " + Base64.getEncoder().encodeToString(("bob" + ":" + "password").getBytes());

    // Then
    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header))
              .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void basicNoColonFail() {
    // Given
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";

    // then
    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header))
              .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void basicNoIdFail() {
    // Given
    var header = "   Basic " + Base64.getEncoder().encodeToString(("" + ":" + "password").getBytes()) + "   ";

    // Then
    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void basicNoPasswordFail() {
    // Given
    var header = "   Basic " + Base64.getEncoder().encodeToString(("bob" + ":" + "").getBytes()) + "   ";

    // then
    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header))
              .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  @Test
  void noBasic() {
    // when
    var header = "   Base " + Base64.getEncoder().encodeToString(("bob" + "" + "password").getBytes()) + "   ";
    // then
    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void bearerSuccess() {
    // Given
    var token = "xyz.xyz.zyz";
    var header = "Bearer " + token;

    // When
    var credential = HttpUtils.credential(header);

    // Then
    Assertions.assertThat(credential).isEqualTo(new Token(token));
  }

  @Test
  void noBearer() {
    // Given
    var token = "xyz.xyz.zyz";
    var header = "Bearr " + token;

    // Then

    Assertions.assertThatThrownBy(() -> HttpUtils.credential(header)).isInstanceOf(NoSuchElementException.class);
  }
}
