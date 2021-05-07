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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;

import io.github.alttpj.emu2api.source.api.EmulatorSource;
import io.github.alttpj.emu2api.source.config.base.Emulator;
import io.github.alttpj.emu2api.source.config.base.EmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class RetroArchEmulatorSource implements EmulatorSource {

  private static final Logger LOG =
      Logger.getLogger(RetroArchEmulatorSource.class.getCanonicalName());

  private static final Set<RetroArchConnection> CONNECTIONS = new HashSet<>();
  private static final String SOURCE_NAME = "RetroArch";

  @Inject private RetroArchDeviceFactory deviceFactory;

  @Inject private GeneralConfig generalConfig;

  @Inject
  @Emulator(name = SOURCE_NAME)
  private EmulatorConfig retroArchConfig;

  @Override
  public String getSourceName() {
    return SOURCE_NAME;
  }

  @Override
  public List<String> getDiscoveredDeviceNames() {
    if (!this.retroArchConfig.isEnabled()) {
      LOG.log(Level.FINE, "not enabled.");
      return List.of();
    }

    this.updateDevicesList();

    return CONNECTIONS.stream()
        // todo: make the canConnect wrapped with a timout.
        .filter(RetroArchConnection::canConnect)
        .map(RetroArchConnection::getDevice)
        .map(RetroArchDevice::getName)
        .collect(toUnmodifiableList());
  }

  @Override
  public boolean hasDevice(final String wantedDeviceName) {
    return CONNECTIONS.stream()
        .anyMatch(conn -> wantedDeviceName.equals(conn.getDevice().getName()));
  }

  @Override
  public List<String> getInfo(final String deviceName, final List<String> commandParameters) {
    final Optional<RetroArchConnection> deviceOpt =
        CONNECTIONS.stream()
            .filter(conn -> deviceName.equals(conn.getDevice().getName()))
            .findAny();
    if (deviceOpt.isEmpty()) {
      throw new IllegalStateException("Device got disconnected: " + deviceName + "!");
    }

    final RetroArchConnection connection = deviceOpt.orElseThrow();

    final List<String> resultList = new ArrayList<>();
    resultList.add(connection.getVersion());
    resultList.addAll(RetroArchConnection.DEFAULT_FLAGS);

    return unmodifiableList(resultList);
  }

  @Override
  public ByteBuffer getAddr(final String deviceName, final List<String> commandParameters) {
    final Optional<RetroArchConnection> deviceOpt =
        CONNECTIONS.stream()
            .filter(conn -> deviceName.equals(conn.getDevice().getName()))
            .findAny();
    if (deviceOpt.isEmpty()) {
      throw new IllegalStateException("Device got disconnected: " + deviceName + "!");
    }

    final RetroArchConnection connection = deviceOpt.orElseThrow();

    final String size = commandParameters.get(1);
    final int length = Integer.parseInt(size, 16);
    final GenericResponseReader reader = new GenericResponseReader(length);
    final String addressString = commandParameters.get(0);
    final RetroArchAddressServiceImpl addressService = new RetroArchAddressServiceImpl(true);
    final int retroArchAddress =
        addressService.translateAddressToRetroArch(new BigInteger(addressString, 16).intValue());
    final String retroArchHexString = new BigDecimal(retroArchAddress).unscaledValue().toString(16);
    final String command =
        String.format(Locale.ENGLISH, "READ_CORE_MEMORY %s %d", retroArchHexString, length);
    final byte[] connReturn = connection.sendCommand(reader, command);

    return ByteBuffer.wrap(connReturn);
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
        instances.stream().map(this.deviceFactory::build).collect(toSet());
    return devices;
  }

  private void addAndRemoveDevices(final Set<RetroArchDevice> devices) {
    final Set<RetroArchConnection> connectionSet =
        devices.stream().map(RetroArchConnection::new).collect(toSet());

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

  public void setRetroArchConfig(final EmulatorConfig retroArchConfig) {
    this.retroArchConfig = retroArchConfig;
  }
}
