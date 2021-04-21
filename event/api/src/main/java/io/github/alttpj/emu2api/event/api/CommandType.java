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

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.StringJoiner;

public enum CommandType {
  /** Queries RetroArch for the version. */
  VERSION(List.of());

  private final List<Class<?>> parameterTypes;

  CommandType(final List<Class<?>> parameterTypes) {
    this.parameterTypes = unmodifiableList(parameterTypes);
  }

  public List<Class<?>> getParameterTypes() {
    return this.parameterTypes;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CommandType.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("parameterTypes=" + this.parameterTypes)
        .toString();
  }
}
