package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.core.Server;
import io.github.henryssondaniel.teacup.engine.Fixture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class TeacupTest {

  private static final String CLIENT = "client";
  private static final String SERVER = "server";

  @Test
  void getClient() {
    Assertions.assertThatExceptionOfType(AssertionFailedError.class)
        .isThrownBy(() -> Teacup.getClient(Object.class, CLIENT));

    var executor = ExecutorHolder.getExecutor();
    executor.executeFixture(TestClass.class.getAnnotation(Fixture.class));

    var client = new Object();
    executor.getCurrentSetup().orElseThrow().putClient(CLIENT, client);

    assertThat(Teacup.getClient(Object.class, CLIENT)).isSameAs(client);
  }

  @Test
  void getServer() {
    Assertions.assertThatExceptionOfType(AssertionFailedError.class)
        .isThrownBy(() -> Teacup.getServer(Server.class, SERVER));

    var executor = ExecutorHolder.getExecutor();
    executor.executeFixture(TestClass.class.getAnnotation(Fixture.class));

    Server server = new TestServer();
    executor.getCurrentSetup().orElseThrow().putServer(SERVER, server);

    assertThat(Teacup.getServer(TestServer.class, SERVER)).isSameAs(server);
  }

  @Fixture(TestSetup.class)
  private static final class TestClass {
    // Empty
  }

  private static final class TestServer implements Server {
    private static final Logger LOGGER = Logger.getLogger(TestServer.class.getName());

    @Override
    public void setUp() {
      LOGGER.log(Level.FINE, "Set up");
    }

    @Override
    public void tearDown() {
      LOGGER.log(Level.FINE, "Tear down");
    }
  }
}
