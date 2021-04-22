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

package io.github.alttpj.emu2api.source.retroarch;

import io.github.alttpj.emu2api.event.api.Command;
import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.source.api.EmulatorSource;
import io.github.alttpj.emu2api.source.api.config.Emulator;
import io.github.alttpj.emu2api.source.api.config.EmulatorConfig;
import io.github.alttpj.emu2api.source.api.config.GeneralConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class RetroArchEmulatorSource implements EmulatorSource {

  private static final Logger LOG =
      Logger.getLogger(RetroArchEmulatorSource.class.getCanonicalName());

  @Inject
  private GeneralConfig generalConfig;

  @Inject
  @Emulator(name = "RetroArch")
  private EmulatorConfig retroArchConfig;

  @Inject
  private Event<CommandResponse> commandResponseEvent;

  public void commandRequest(
      final @Observes @Command(type = CommandType.DEVICE_LIST) CommandRequest event) {
    if (!this.retroArchConfig.isEnabled()) {
      LOG.log(Level.FINE, "not enabled.");
      return;
    }

    final CommandResponse empty =
        CommandResponse.builder()
            .requestId(event.getRequestId())
            .commandType(event.getCommandType())
            .build();

    this.commandResponseEvent.fire(empty);
  }
}
