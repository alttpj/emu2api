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
import jakarta.enterprise.event.ObservesAsync;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class Emu2ApiCommandDispatcher {

  private static final Logger LOG =
      Logger.getLogger(Emu2ApiCommandDispatcher.class.getCanonicalName());

  @Inject private ExecutorService executorService;

  @Inject @Any private Instance<EmulatorSource> emulators;

  public Emu2ApiCommandDispatcher() {
    // cdi
  }

  public void commandDeviceList(
      final @ObservesAsync @Command(type = CommandType.DEVICE_LIST) CommandRequest event) {
    final List<CompletableFuture<Void>> collect =
        this.emulators.stream()
            .map(emu -> this.runOrTimeout(emu, event))
            .collect(Collectors.toList());

    // wait for the requests to complete (respecting the timeouts),
    // otherwise the socket would answer with an empty response as no emulator
    // would have hit the commandRequest.addReturnParameters() call.
    CompletableFuture.allOf(collect.toArray(CompletableFuture[]::new)).join();
  }

  private CompletableFuture<Void> runOrTimeout(
      final EmulatorSource source, final CommandRequest commandRequest) {
    return CompletableFuture
        // ask each emulator to return the list of known devices.
        .supplyAsync(
            () -> this.getGetDiscoveredDeviceNames(source, commandRequest), this.executorService)
        // set a timeout for the emulator.
        .orTimeout(200L, TimeUnit.MILLISECONDS)
        // on error, log and return an empty result.
        .handleAsync(
            (result, error) -> this.resultOrLogAndEmpty(source, commandRequest, result, error),
            this.executorService)
        // handle the result over to the command.
        .thenAccept(rsp -> commandRequest.addReturnParameters(source.getSourceName(), rsp));
  }

  /**
   * Logs an error and returns an empty response, otherwise returns the response.
   *
   * @param source the source which answered or had an error.
   * @param commandRequest the request which led to the source's result.
   * @param result if non-null, the source answered successfully.
   * @param error if non-null, the source did not answer in time or hit an exception.
   * @return a response either way.
   */
  private CommandResponse resultOrLogAndEmpty(
      final EmulatorSource source,
      final CommandRequest commandRequest,
      final CommandResponse result,
      final Throwable error) {
    if (error != null) {
      LOG.log(
          Level.WARNING,
          error,
          () ->
              String.format(
                  Locale.ENGLISH, "Source [%s] did not respond in time.", source.getSourceName()));

      return CommandResponse.builder()
          .requestId(commandRequest.getRequestId())
          .commandType(commandRequest.getCommandType())
          .build();
    }

    return result;
  }

  private CommandResponse getGetDiscoveredDeviceNames(
      final EmulatorSource source, final CommandRequest commandRequest) {
    try {
      final Set<String> discoveredDeviceNames = source.getDiscoveredDeviceNames();

      return CommandResponse.builder()
          .requestId(commandRequest.getRequestId())
          .commandType(commandRequest.getCommandType())
          .addAllReturnParameters(discoveredDeviceNames)
          .build();
    } catch (final RuntimeException rtEx) {
      return CommandResponse.builder()
          .requestId(commandRequest.getRequestId())
          .commandType(commandRequest.getCommandType())
          .failedWith(rtEx)
          .build();
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

  @Override
  public String toString() {
    return new StringJoiner(", ", Emu2ApiCommandDispatcher.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("executorService=" + this.executorService)
        .add("emulators=" + this.emulators)
        .toString();
  }
}
