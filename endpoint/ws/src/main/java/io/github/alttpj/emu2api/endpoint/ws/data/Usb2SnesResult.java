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

package io.github.alttpj.emu2api.endpoint.ws.data;

import static java.util.Collections.unmodifiableList;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.StringJoiner;

public class Usb2SnesResult {

  private final List<String> results;

  public Usb2SnesResult(final List<String> results) {
    this.results = unmodifiableList(results);
  }

  @JsonbProperty("Results")
  public List<String> getResults() {
    return this.results;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Usb2SnesResult.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("results=" + this.results)
        .toString();
  }
}
