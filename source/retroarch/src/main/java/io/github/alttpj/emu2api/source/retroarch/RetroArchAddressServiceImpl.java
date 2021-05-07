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

import static java.lang.Integer.compareUnsigned;

public class RetroArchAddressServiceImpl implements RetroArchAddressService {

  private final boolean hasRomAccess;

  private final RomType romType;

  public RetroArchAddressServiceImpl() {
    this(false, RomType.LoROM);
  }

  public RetroArchAddressServiceImpl(final boolean hasRomAccess) {
    this(hasRomAccess, RomType.LoROM);
  }

  public RetroArchAddressServiceImpl(final boolean hasRomAccess, final RomType romType) {
    this.hasRomAccess = hasRomAccess;
    this.romType = romType;
  }

  @Override
  public int translateAddressToRetroArch(final int address) {
    // Rom access
    if (compareUnsigned(address, 0xE00000) < 0) {
      return this.translateToRom(address);
    }

    // WRAM access
    if (compareUnsigned(address, 0xF50000) >= 0 && compareUnsigned(address, 0xF70000) <= 0) {
      return this.translateToWram(address);
    }

    if (compareUnsigned(address, 0xE00000) >= 0 && compareUnsigned(address, 0xF70000) < 0) {
      return this.translateToSram(address);
    }

    return -1;
  }

  protected int translateToSram(final int address) {
    if (!this.hasRomAccess) {
      return address - 0xE00000 + 0x20000;
    }

    if (this.romType == RomType.LoROM) {
      return address - 0xE00000 + 0x700000;
    }

    return this.loromSramPcToSnes(address - 0xE00000);
  }

  protected int translateToWram(final int address) {
    if (this.hasRomAccess) {
      return address - 0xF50000 + 0x7E0000;
    } else {
      return address - 0xF50000;
    }
  }

  protected int translateToRom(final int address) {
    if (!this.hasRomAccess) {
      return -1;
    }

    switch (this.romType) {
      case LoROM:
        return 0x800000 + (address + (0x8000 * ((address + 0x8000) / 0x8000)));
      case HiROM:
        if (address < 0x400000) {
          return address + 0xC00000;
        } else {
          // exhirom
          return address + 0x400000;
        }
      default:
        throw new UnsupportedOperationException("Not implemented: " + this.romType);
    }
  }

  protected int loromSramPcToSnes(final int pcAddr) {
    final int chuckNb = pcAddr / 0x8000;
    final int rest = pcAddr % 0x8000;

    if (chuckNb <= 0xD) {
      return ((0x70 + chuckNb) << 16) + rest;
    }

    if (chuckNb == 0xE || chuckNb == 0xF) {
      return ((0xF0 + chuckNb) << 16) + rest;
    }

    return -1;
  }
}
