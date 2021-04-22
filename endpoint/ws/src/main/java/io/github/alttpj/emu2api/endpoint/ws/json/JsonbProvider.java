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

package io.github.alttpj.emu2api.endpoint.ws.json;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.util.StringJoiner;

@ApplicationScoped
public class JsonbProvider {

  private final Jsonb jsonb;

  public JsonbProvider() {
    // Create custom configuration
    final JsonbConfig config = new JsonbConfig().withStrictIJSON(true);

    // Create Jsonb with custom configuration
    this.jsonb = JsonbBuilder.create(config);
  }

  @Produces
  public Jsonb getJsonbProvider() {
    return this.jsonb;
  }

  public void close(final @Disposes Jsonb jsonb) throws Exception {
    if (jsonb == null) {
      return;
    }

    jsonb.close();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", JsonbProvider.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("jsonb=" + this.jsonb)
        .toString();
  }
}
