package org.teacup.engine.junit;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.discovery.DiscoverySelectorResolver;
import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

/**
 * The teacup test engine.
 *
 * @since 1.0
 */
public class TeacupTestEngine extends HierarchicalTestEngine<JupiterEngineExecutionContext> {
  private static final String ID = "teacup";
  private static final Logger LOGGER = Logger.getLogger(TeacupTestEngine.class.getName());

  @Override
  public TestDescriptor discover(EngineDiscoveryRequest engineDiscoveryRequest, UniqueId uniqueId) {
    LOGGER.log(Level.SEVERE, "Discover using unique ID: " + uniqueId);

    TestDescriptor engineDescriptor = new JupiterEngineDescriptor(uniqueId);

    new DiscoverySelectorResolver().resolveSelectors(engineDiscoveryRequest, engineDescriptor);
    Utils.group(engineDescriptor);

    return engineDescriptor;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  protected JupiterEngineExecutionContext createExecutionContext(
      ExecutionRequest executionRequest) {
    return new JupiterEngineExecutionContext(
        executionRequest.getEngineExecutionListener(),
        executionRequest.getConfigurationParameters());
  }
}
