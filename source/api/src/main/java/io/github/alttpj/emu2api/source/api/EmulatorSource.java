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

package io.github.alttpj.emu2api.source.api;

import io.github.alttpj.emu2api.event.api.CommandRequestEvent;
import jakarta.enterprise.event.Observes;
import java.util.List;

/** An emulator source is an implementation of a backend, like RetroArch or sd2snes. */
public interface EmulatorSource {

  /**
   * This method is called every time a command shall be executed.
   *
   * @param event the command to be executed.
   */
  void commandRequest(@Observes CommandRequestEvent event);

  /**
   * Returns the list of Emulators which this source is connected to.
   *
   * @return a list of emulators this source is connected to.
   */
  List<Emulator> getConnectedTo();
}
