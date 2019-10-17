package io.github.henryssondaniel.teacup.engine.junit;

import io.github.henryssondaniel.teacup.engine.Executor;
import io.github.henryssondaniel.teacup.engine.ExecutorFactory;

enum ExecutorHolder {
  ;

  private static final Executor EXECUTOR = ExecutorFactory.create();

  static Executor getExecutor() {
    return EXECUTOR;
  }
}
