package org.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.teacup.core.Executor;
import org.teacup.core.Setup;
import org.teacup.core.Utils;

class TeacupTest {
  private static final String CLIENT = "value";
  private static final String NAME = "name";

  @BeforeEach
  void beforeEach() throws IllegalAccessException, NoSuchFieldException {
    var setup = mock(Setup.class);
    when(setup.getClients()).thenReturn(Collections.singletonMap(NAME, CLIENT));

    var executor = mock(Executor.class);
    when(executor.getCurrentSetup()).thenReturn(Optional.of(setup));

    Utils.setField(Teacup.class, null, "EXECUTOR", executor);
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
}
