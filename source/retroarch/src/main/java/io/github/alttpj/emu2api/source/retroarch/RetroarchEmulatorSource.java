/*
 * Copyright 2021-${year} the ALttPJ Team @ https://github.com/alttpj
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

import io.github.alttpj.emu2api.event.api.CommandRequestEvent;
import io.github.alttpj.emu2api.event.api.CommandResponseEvent;
import io.github.alttpj.emu2api.source.api.Emulator;
import io.github.alttpj.emu2api.source.api.EmulatorSource;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.List;

public class RetroarchEmulatorSource implements EmulatorSource {

  @Inject private Event<CommandResponseEvent> commandResponseEvent;

  @Override
  public void commandRequest(@Observes final CommandRequestEvent event) {
    // TODO: implement
    throw new UnsupportedOperationException(
        "not yet implemented: [io.github.alttpj.emu2api.source.retroarch.RetroarchEmulatorSource::commandRequest].");
  }

  @Override
  public List<Emulator> getConnectedTo() {
    // TODO: implement
    throw new UnsupportedOperationException(
        "not yet implemented: [io.github.alttpj.emu2api.source.retroarch.RetroarchEmulatorSource::getConnectedTo].");
  }
}
