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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

public class Usb2SnesRequestEncoder implements Encoder.TextStream<Object> {

  private static final Jsonb JSON = JsonbBuilder.create();

  @Override
  public void encode(final Object object, final Writer writer) throws EncodeException, IOException {
    try {
      JSON.toJson(object, writer);
    } catch (final JsonbException jsonbEx) {
      throw new IOException(jsonbEx);
    }
  }

  @Override
  public void init(final EndpointConfig config) {
    // noop
  }

  @Override
  public void destroy() {
    // noop
  }
}
