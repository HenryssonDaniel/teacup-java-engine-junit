package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.engine.DefaultSetup;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSetup extends DefaultSetup {
  private static final Logger LOGGER = Logger.getLogger(TestSetup.class.getName());

  @Override
  public void initialize() {
    LOGGER.log(Level.FINE, "Initialize");
  }
}
