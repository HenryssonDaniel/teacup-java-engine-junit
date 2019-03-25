package org.teacup.engine.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.teacup.core.DefaultSetup;

public class TestSetup extends DefaultSetup {
  private static final Logger LOGGER = Logger.getLogger(TestSetup.class.getName());

  @Override
  public void initialize() {
    LOGGER.log(Level.FINE, "Initialize");

    putClient(TeacupTest.NAME, TeacupTest.CLIENT);
    putServer(TeacupTest.NAME, TeacupTest.SERVER);
  }
}
