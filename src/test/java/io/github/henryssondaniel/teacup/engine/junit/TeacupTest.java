package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.henryssondaniel.teacup.core.Server;
import io.github.henryssondaniel.teacup.engine.Fixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class TeacupTest {
  static final String CLIENT = "value";
  static final String NAME = "name";
  static final Server SERVER = mock(Server.class);

  private static final String MESSAGE = "Could not retrieve the %s";

  @BeforeEach
  @SuppressWarnings("unchecked")
  void beforeEach() {
    var fixture = mock(Fixture.class);
    when(fixture.value()).thenReturn((Class) TestSetup.class);

    ExecutorHolder.getExecutor().executeFixture(fixture);
  }

  @Test
  void getClient() {
    assertThat(Teacup.getClient(String.class, NAME)).isSameAs(CLIENT);
  }

  @Test
  void getClientWhenNotExist() {
    try {
      Teacup.getClient(String.class, "server");
      Assertions.fail();
    } catch (AssertionFailedError assertionFailedError) {
      assertThat(assertionFailedError.getMessage()).isEqualTo(String.format(MESSAGE, "client"));
    }
  }

  @Test
  void getServer() {
    assertThat(Teacup.getServer(Server.class, NAME)).isSameAs(SERVER);
  }

  @Test
  void getServerWhenNotExist() {
    try {
      Teacup.getServer(Server.class, "client");
      Assertions.fail();
    } catch (AssertionFailedError assertionFailedError) {
      assertThat(assertionFailedError.getMessage()).isEqualTo(String.format(MESSAGE, "server"));
    }
  }
}
