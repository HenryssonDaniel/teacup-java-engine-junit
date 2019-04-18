package io.github.henryssondaniel.teacup.engine.junit;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.platform.engine.ConfigurationParameters;

class TestConfigurationParameters implements ConfigurationParameters {
  private static final Logger LOGGER =
      Logger.getLogger(TestConfigurationParameters.class.getName());

  @Override
  public Optional<String> get(String key) {
    LOGGER.log(Level.FINE, "Get");
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> getBoolean(String key) {
    LOGGER.log(Level.FINE, "Get boolean");
    return Optional.empty();
  }

  @Override
  public int size() {
    LOGGER.log(Level.FINE, "Size");
    return 0;
  }
}
