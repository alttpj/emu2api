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

package io.github.alttpj.emu2api.source.dispatcher;

import io.github.alttpj.emu2api.event.api.Command;
import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.source.api.EmulatorSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class Emu2ApiCommandDispatcher {

  private ExecutorService executorService;

  @Inject @Any private Instance<EmulatorSource> emulators;

  @Inject private Event<CommandResponse> commandResponseEvent;

  public Emu2ApiCommandDispatcher() {
    // cdi
  }

  public void commandDeviceList(
      final @Observes @Command(type = CommandType.DEVICE_LIST) CommandRequest event) {
    final List<CompletableFuture<Set<String>>> collect =
        this.emulators.stream().map(this::runOrTimeout).collect(Collectors.toList());

    CompletableFuture.allOf(collect.toArray(CompletableFuture[]::new)).join();

    final Set<String> onlineDevices =
        collect.stream()
            .map(CompletableFuture::join)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    final CommandResponse empty =
        CommandResponse.builder()
            .requestId(event.getRequestId())
            .commandType(event.getCommandType())
            .addAllReturnParameters(onlineDevices)
            .build();

    this.commandResponseEvent.fire(empty);
  }

  private CompletableFuture<Set<String>> runOrTimeout(final EmulatorSource source) {
    this.ensureExecutorService();
    return CompletableFuture.supplyAsync(source::getDiscoveredDeviceNames, this.executorService)
        .completeOnTimeout(Set.of(), 200L, TimeUnit.MILLISECONDS);
  }

  private void ensureExecutorService() {
    if (this.executorService == null) {
      synchronized (this) {
        if (this.executorService == null) {
          this.executorService = Executors.newWorkStealingPool();
        }
      }
    }

    if (this.executorService == null) {
      throw new IllegalStateException("no executor service");
    }
  }

  public Instance<EmulatorSource> getEmulators() {
    return this.emulators;
  }

  public void setEmulators(final Instance<EmulatorSource> emulators) {
    this.emulators = emulators;
  }

  public ExecutorService getExecutorService() {
    return this.executorService;
  }

  public void setExecutorService(final ExecutorService executorService) {
    this.executorService = executorService;
  }

  public Event<CommandResponse> getCommandResponseEvent() {
    return this.commandResponseEvent;
  }

  public void setCommandResponseEvent(final Event<CommandResponse> commandResponseEvent) {
    this.commandResponseEvent = commandResponseEvent;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Emu2ApiCommandDispatcher.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("executorService=" + this.executorService)
        .add("emulators=" + this.emulators)
        .add("commandResponseEvent=" + this.commandResponseEvent)
        .toString();
  }
}
