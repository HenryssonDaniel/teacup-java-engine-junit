package org.teacup.engine.junit;

import io.github.henryssondaniel.teacup.core.Executor;
import io.github.henryssondaniel.teacup.core.ExecutorFactory;

enum ExecutorHolder {
  ;

  private static final Executor EXECUTOR = ExecutorFactory.create();

  static Executor getExecutor() {
    return EXECUTOR;
  }
}
