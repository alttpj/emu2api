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
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.source.api.EmulatorSource;
import io.github.alttpj.emu2api.utils.async.ExecutorName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

@ApplicationScoped
public class Emu2ApiCommandDispatcher extends AbstractCommandDelegatingDispatcher {

  @Inject
  @ExecutorName("concurrent/dispatcher")
  private ExecutorService executorService;

  @Inject @Any private Instance<EmulatorSource> emulators;

  public Emu2ApiCommandDispatcher() {
    // cdi
  }

  public void commandDeviceList(
      final @ObservesAsync @Command(type = CommandType.DEVICE_LIST) CommandRequest event) {
    final BiFunction<EmulatorSource, CommandRequest, Set<String>> executable =
        (src, cmd) -> src.getDiscoveredDeviceNames();

    this.runCommands(event, executable);
  }

  public void commandInfo(
      final @ObservesAsync @Command(type = CommandType.INFO) CommandRequest event) {
    final BiFunction<EmulatorSource, CommandRequest, Set<String>> executable =
        (src, cmd) -> src.getInfo(cmd.getTargetDevice().orElseThrow(), cmd.getCommandParameters());

    this.runCommands(event, executable);
  }

  @Override
  public Instance<EmulatorSource> getEmulators() {
    return this.emulators;
  }

  public void setEmulators(final Instance<EmulatorSource> emulators) {
    this.emulators = emulators;
  }

  @Override
  public ExecutorService getExecutorService() {
    return this.executorService;
  }

  public void setExecutorService(final ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Emu2ApiCommandDispatcher.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("executorService=" + this.executorService)
        .add("emulators=" + this.emulators)
        .toString();
  }
}
