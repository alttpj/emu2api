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
import io.github.alttpj.emu2api.source.config.base.Emulator;
import io.github.alttpj.emu2api.source.config.base.EmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class RetroArchEmulatorSource implements EmulatorSource {

  private static final Logger LOG =
      Logger.getLogger(RetroArchEmulatorSource.class.getCanonicalName());

  private static final Set<RetroArchConnection> CONNECTIONS = new HashSet<>();

  @Inject private RetroArchDeviceFactory deviceFactory;

  @Inject private GeneralConfig generalConfig;

  @Inject
  @Emulator(name = "RetroArch")
  private EmulatorConfig retroArchConfig;

  @Inject private Event<CommandResponse> commandResponseEvent;

  public void commandDeviceList(
      final @Observes @Command(type = CommandType.DEVICE_LIST) CommandRequest event) {
    if (!this.retroArchConfig.isEnabled()) {
      LOG.log(Level.FINE, "not enabled.");
      return;
    }

    this.updateDevicesList();

    final Set<String> onlineDevices =
        CONNECTIONS.stream()
            .filter(RetroArchConnection::canConnect)
            .map(rac -> rac.getDevice().getName())
            .collect(Collectors.toSet());

    final CommandResponse empty =
        CommandResponse.builder()
            .requestId(event.getRequestId())
            .commandType(event.getCommandType())
            .addAllReturnParameters(onlineDevices)
            .build();

    this.commandResponseEvent.fire(empty);
  }

  public Set<RetroArchDevice> updateDevicesList() {
    if (!this.retroArchConfig.isEnabled()) {
      return Set.of();
    }

    final Set<RetroArchDevice> devices = this.buildWantedDevices();

    this.addAndRemoveDevices(devices);

    return devices;
  }

  private Set<RetroArchDevice> buildWantedDevices() {
    final List<Map<String, Object>> instances =
        (List<Map<String, Object>>)
            this.retroArchConfig
                .getConfigurationMap()
                .getOrDefault(
                    "instances", List.of(this.deviceFactory.getDefaultConfigurationMap()));

    final Set<RetroArchDevice> devices =
        instances.stream().map(this.deviceFactory::build).collect(Collectors.toSet());
    return devices;
  }

  private void addAndRemoveDevices(final Set<RetroArchDevice> devices) {
    final Set<RetroArchConnection> connectionSet =
        devices.stream().map(RetroArchConnection::new).collect(Collectors.toSet());

    CONNECTIONS.addAll(connectionSet);
    for (final RetroArchConnection connection : CONNECTIONS) {
      if (connectionSet.contains(connection)) {
        continue;
      }

      connection.close();
    }
    CONNECTIONS.retainAll(connectionSet);
  }

  public EmulatorConfig getRetroArchConfig() {
    return this.retroArchConfig;
  }

  public GeneralConfig getGeneralConfig() {
    return this.generalConfig;
  }

  public void setGeneralConfig(final GeneralConfig generalConfig) {
    this.generalConfig = generalConfig;
  }

  public void setRetroArchConfig(
      final EmulatorConfig retroArchConfig) {
    this.retroArchConfig = retroArchConfig;
  }
}
