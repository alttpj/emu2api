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

import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.source.api.EmulatorSource;
import jakarta.enterprise.inject.Instance;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractCommandDelegatingDispatcher {

  private static final Logger LOG =
      Logger.getLogger(AbstractCommandDelegatingDispatcher.class.getCanonicalName());

  protected <T> void runCommands(
      final CommandRequest event, final BiFunction<EmulatorSource, CommandRequest, T> executable) {
    final List<CompletableFuture<Void>> collect =
        this.getEmulators().stream()
            .map(emu -> this.runOrTimeout(executable, emu, event))
            .collect(Collectors.toList());

    // wait for the requests to complete (respecting the timeouts),
    // otherwise the socket would answer with an empty response as no emulator
    // would have hit the commandRequest.addReturnParameters() call.
    CompletableFuture.allOf(collect.toArray(CompletableFuture[]::new)).join();
  }

  private <T> CompletableFuture<Void> runOrTimeout(
      final BiFunction<EmulatorSource, CommandRequest, T> executable,
      final EmulatorSource source,
      final CommandRequest commandRequest) {
    final Optional<String> targetDevice = commandRequest.getTargetDevice();
    if (targetDevice.isPresent() && !source.hasDevice(targetDevice.orElseThrow())) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture
        // ask each emulator to return the list of known devices.
        .supplyAsync(
            () -> this.wrapRuntimeException(executable, source, commandRequest),
            this.getExecutorService())
        // set a timeout for the emulator.
        .orTimeout(200L, TimeUnit.MILLISECONDS)
        // on error, log and return an empty result.
        .handleAsync(
            (result, error) -> this.resultOrLogAndEmpty(source, commandRequest, result, error),
            this.getExecutorService())
        // handle the result over to the command.
        .thenAccept(rsp -> this.doAppendReturnParameters(source, commandRequest, rsp));
  }

  private void doAppendReturnParameters(
      final EmulatorSource source, final CommandRequest commandRequest, final CommandResponse rsp) {
    if (commandRequest.isBinaryResponse()) {
      commandRequest.setBinaryResponse(rsp.getBinaryReturnParameter());
    } else {
      commandRequest.addReturnParameters(source.getSourceName(), rsp);
    }
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

  private <T> CommandResponse wrapRuntimeException(
      final BiFunction<EmulatorSource, CommandRequest, T> executable,
      final EmulatorSource source,
      final CommandRequest commandRequest) {
    try {
      final T discoveredDeviceNames = executable.apply(source, commandRequest);

      if (discoveredDeviceNames instanceof List) {
        return CommandResponse.builder()
            .requestId(commandRequest.getRequestId())
            .commandType(commandRequest.getCommandType())
            .addAllReturnParameters((List<String>) discoveredDeviceNames)
            .build();
      } else if (discoveredDeviceNames instanceof ByteBuffer) {
        return CommandResponse.builder()
            .requestId(commandRequest.getRequestId())
            .commandType(commandRequest.getCommandType())
            .binaryReturnParameter((ByteBuffer) discoveredDeviceNames)
            .build();
      } else {
        throw new UnsupportedOperationException(
            "Unknown return type class: " + discoveredDeviceNames.getClass());
      }

    } catch (final RuntimeException rtEx) {
      LOG.log(
          Level.WARNING,
          rtEx,
          () ->
              String.format(
                  Locale.ENGLISH, "Source [%s] did throw exception.", source.getSourceName()));
      return CommandResponse.builder()
          .requestId(commandRequest.getRequestId())
          .commandType(commandRequest.getCommandType())
          .failedWith(rtEx)
          .build();
    }
  }

  public abstract Instance<EmulatorSource> getEmulators();

  public abstract ExecutorService getExecutorService();
}
