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

package io.github.alttpj.emu2api.event.api;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
abstract class AbstractCommand {

  public abstract CommandType getCommandType();

  public abstract List<Object> getCommandParameters();

  @Value.Check
  void checkCommandValid() {
    final CommandType commandType = this.getCommandType();
    final List<Object> commandParameters = this.getCommandParameters();

    final List<Class<?>> parameterTypes = commandType.getParameterTypes();
    final long minParameterCount =
        parameterTypes.stream().filter(type -> !type.isAssignableFrom(Optional.class)).count();

    if (commandParameters.size() < minParameterCount) {
      final String message =
          String.format(
              Locale.ENGLISH,
              "Expected at least [%d] parameters for command [%s], but got [%d].",
              minParameterCount,
              commandType,
              commandParameters.size());

      throw new IllegalStateException(message);
    }
  }
}
