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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class GenericResponseReader extends AbstractUdpResponseReader {

  private final int bufferSize;
  private ByteBuffer responseData;

  public GenericResponseReader(final int bufferSize) {
    this.bufferSize =
        32 /* preamble */
            + 1 /* whitespace */
            + bufferSize * 3 /* hex encoded with leading space */;
  }

  @Override
  protected int getReadBufferSize() {
    return this.bufferSize;
  }

  @Override
  protected void processResponse() {
    // no operation, return raw
    final String out = new String(super.getReadBuffer(), StandardCharsets.UTF_8);
    final String[] splits = out.split(" ", 3);
    if (splits.length < 3) {
      this.responseData = ByteBuffer.wrap(super.getReadBuffer());
      return;
    }

    final String hexResponse = splits[2].replaceAll(" ", "").trim();
    final byte[] responseAsBytes = hexStringToByteArray(hexResponse);
    this.responseData = ByteBuffer.wrap(responseAsBytes);
  }

  @Override
  public byte[] getParsedResponse() {
    this.responseData.flip();
    return this.responseData.array();
  }

  /* s must be an even-length string. */
  public static byte[] hexStringToByteArray(final String hexIn) {
    final int len = hexIn.length();
    final byte[] data = new byte[len / 2];

    for (int index = 0; index < len; index += 2) {
      data[index / 2] =
          (byte)
              ((Character.digit(hexIn.charAt(index), 16) << 4)
                  + Character.digit(hexIn.charAt(index + 1), 16));
    }

    return data;
  }
}
