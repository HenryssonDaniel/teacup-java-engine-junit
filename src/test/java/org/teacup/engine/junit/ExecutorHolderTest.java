package org.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.teacup.core.Executor;

class ExecutorHolderTest {
  @Test
  void getExecutor() {
    assertThat(ExecutorHolder.getExecutor()).isInstanceOf(Executor.class);
  }
}
