/*
 * Copyright 2021-2021 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.emu2api.utils.async;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExecutorProducer {

  private static final Logger LOG = Logger.getLogger(ExecutorProducer.class.getCanonicalName());

  private static final Map<String, ExecutorService> PRODUCED = new ConcurrentHashMap<>();

  private String namedOrClassName(final InjectionPoint injectionPoint) {
    return Optional.ofNullable(injectionPoint.getAnnotated().getAnnotation(ExecutorName.class))
        .map(ExecutorName::value)
        .orElseThrow();
  }

  @Produces
  @Dependent
  @ExecutorName
  public ExecutorService produceExecutorService(final InjectionPoint injectionPoint) {
    final String name = this.namedOrClassName(injectionPoint);
    if (PRODUCED.get(name) != null) {
      return PRODUCED.get(name);
    }
    final ExecutorService executorService = Executors.newWorkStealingPool();
    PRODUCED.put(name, executorService);

    LOG.log(
        Level.INFO,
        () ->
            String.format(
                Locale.ENGLISH, "producing pool [%s] with name [%s]", executorService, name));

    return executorService;
  }

  public void disposeExecutorService(@Disposes final ExecutorService executorService) {
    LOG.log(
        Level.INFO, () -> String.format(Locale.ENGLISH, "disposing pool [%s].", executorService));
    executorService.shutdown();
    try {
      executorService.awaitTermination(100L, TimeUnit.MILLISECONDS);
    } catch (final InterruptedException javaLangInterruptedException) {
      Thread.currentThread().interrupt();
      LOG.log(
          Level.WARNING,
          javaLangInterruptedException,
          () -> "Unable to shut down executorService: " + executorService);
    }

    executorService.shutdownNow();
    final Set<String> keys =
        PRODUCED.entrySet().stream()
            .filter(ks -> ks.getValue().equals(executorService))
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    keys.forEach(PRODUCED::remove);
  }
}
