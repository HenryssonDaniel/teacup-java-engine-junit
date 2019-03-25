package org.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.teacup.core.Fixture;
import org.teacup.core.Server;

class TeacupTest {
  static final String CLIENT = "value";
  static final String NAME = "name";
  static final Server SERVER = mock(Server.class);

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
    } catch (AssertionFailedError ignored) {
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
    } catch (AssertionFailedError ignored) {
    }
  }
}
