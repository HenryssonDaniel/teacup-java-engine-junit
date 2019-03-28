package io.github.henryssondaniel.teacup.engine.junit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.henryssondaniel.teacup.core.Executor;
import org.junit.jupiter.api.Test;

class ExecutorHolderTest {
  @Test
  void getExecutor() {
    assertThat(ExecutorHolder.getExecutor()).isInstanceOf(Executor.class);
  }
}
