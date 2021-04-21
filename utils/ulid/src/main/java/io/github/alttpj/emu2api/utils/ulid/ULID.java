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

package io.github.alttpj.emu2api.utils.ulid;

import java.io.Serializable;

/**
 * ULID class which is closer to the UUID class by java.
 *
 * <p>Hides the actual implementation.
 */
public class ULID implements Serializable, Comparable<ULID> {

  private static final long serialVersionUID = 5429716554992866136L;
  private final ULIDImplementation.Value ulid;

  public ULID(final long mostSignificantBits, final long leastSignificantBits) {
    this.ulid = new ULIDImplementation.Value(mostSignificantBits, leastSignificantBits);
  }

  public static ULID fromString(final String ulidString) {
    final ULIDImplementation.Value value = ULIDImplementation.parseULID(ulidString);

    return new ULID(value.getMostSignificantBits(), value.getLeastSignificantBits());
  }

  public static ULID randomULID() {
    return nextULID();
  }

  public static ULID nextULID() {
    final ULIDImplementation.Value ulid = new ULIDImplementation().nextValue();

    return new ULID(ulid.getMostSignificantBits(), ulid.getLeastSignificantBits());
  }

  public long getMostSignificantBits() {
    return this.ulid.getMostSignificantBits();
  }

  public long getLeastSignificantBits() {
    return this.ulid.getLeastSignificantBits();
  }

  public long getTimestamp() {
    return this.ulid.timestamp();
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }

    return this.ulid.equals(((ULID) other).ulid);
  }

  @Override
  public int hashCode() {
    return this.ulid.hashCode();
  }

  @Override
  public int compareTo(final ULID other) {
    return this.ulid.compareTo(other.ulid);
  }

  @Override
  public String toString() {
    return this.ulid.toString();
  }
}
