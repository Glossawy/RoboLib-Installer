/*==================================================================================================
 RoboLib - An Expansion and Improvement Library for WPILibJ
 Copyright (C) 2015  Glossawy

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 =================================================================================================*/


package org.usfirst.frc.team1554.lib.util;

import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.collect.ObjectSet;

import java.util.Map;

/**
 * Small Utility to Determine OS and OS Architecture based on an
 * enumeration of supported OS's and Architectures. Plus other
 * utilities.
 *
 * @author Glossawy
 */
public final class OS {

    // Totally not inspired by java.util.concurrent.TimeUnit

    /**
     * Convenience class for dealing with conversions to and from
     * different units of memory storage. Includes SI Units
     * (Kilobytes, Megabytes, Gigabytes) and non-SI Units (Kibibytes,
     * Mebibytes, Gibibytes) assisted by the common base unit they
     * share, the Byte. <br />
     * <br />
     * Currently this class only deals with 64-bit Integer Values
     * (Although 32-bit Integers can be used).
     *
     * @author Glossawy
     */
    public enum MemoryUnit {
        BYTES("B") {
            @Override
            public long toBytes(long amt) {
                return amt;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toBytes(amt);
            }
        },
        KILOBYTES("KB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_KB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_KB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_MB;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_KB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toKilobytes(amt);
            }
        },
        KIBIBYTES("KiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_KiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_KiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_KiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_MiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toKibibytes(amt);
            }
        },
        MEGABYTES("MB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_MB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_MB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_MB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_MB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toMegabytes(amt);
            }
        },
        MEBIBYTES("MiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_MiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_MiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_MiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_MiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toMebibytes(amt);
            }
        },
        GIGABYTES("GB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_GB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt * C_MB;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_GB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_GB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_GB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toGigabytes(amt);
            }
        },
        GIBIBYTES("GiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_GiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_GiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt * C_MiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_GiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_GiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toGibibytes(amt);
            }
        };

        // SI Unit Memory Size Constants
        static final long C_KB = 1_000;
        static final long C_MB = C_KB * 1_000;
        static final long C_GB = C_MB * 1_000;

        // Non-SI Unit Memory Size Constants
        static final long C_KiB = 1_024;
        static final long C_MiB = C_KiB * 1_024;
        static final long C_GiB = C_MiB * 1_024;

        private final String tag;

        private MemoryUnit(String tag) {
            this.tag = tag;
        }

        /**
         * Convert to Bytes
         *
         * @param amt
         * @return
         */
        public abstract long toBytes(long amt);

        /**
         * Convert to Kilobytes (1000 Bytes or 10^3 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toKilobytes(long amt);

        /**
         * Convert to Kibibytes (1024 Bytes or 2^10 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toKibibytes(long amt);

        /**
         * Convert to Megabytes (1000 Kilobytes or 10^6 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toMegabytes(long amt);

        /**
         * Convert to Mebibytes (1024 Kibibytes or 2^20 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toMebibytes(long amt);

        /**
         * Convert to Gigabytes (1000 Megabytes or 10^9 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toGigabytes(long amt);

        /**
         * Convert to Gibibytes (1024 Mebibytes or 2^30 Bytes)
         *
         * @param amt
         * @return
         */
        public abstract long toGibibytes(long amt);

        /**
         * Convert source unit to this unit.
         *
         * @param amt
         * @param source
         * @return
         */
        public abstract long convert(long amt, MemoryUnit source);

        /**
         * The appropriate tag for the Unit of Memory Storage, e.g.,
         * MB, MiB, GiB, GB, etc.
         *
         * @return
         */
        public String getTag() {
            return this.tag;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }

    public enum Bit {
        BIT_32("x86", "32-bit", 32), BIT_64("x64", "64-bit", 64);

        private final String tag;
        private final String mnemonic;
        private final int num;

        private Bit(String tag, String mnemonic, int num) {
            this.tag = tag;
            this.mnemonic = mnemonic;
            this.num = num;
        }

        public String getTag() {
            return this.tag;
        }

        public int getInt() {
            return this.num;
        }

        public String getMnemonic() {
            return this.mnemonic;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }

    public static final OS WINDOWS = new OS(".exe", "win", "windows");
    public static final OS UNIX = new OS("", "lin", "linux", "nux");
    public static final OS UNSUPPORTED = new OS("");

    private static final Map<OS, String> nameLookup = Maps.newHashMap();

    private static final Runtime rt = Runtime.getRuntime();
    private static final ObjectSet<OS> values = ObjectSet.with(WINDOWS, UNIX, UNSUPPORTED);

    public final String suffix;
    public final String[] aliases;

    static {
        nameLookup.put(WINDOWS, "Windows");
        nameLookup.put(UNIX, "Unix");
        nameLookup.put(UNSUPPORTED, "Unsupported");
    }

    private OS(String executable, String... aliases) {
        this.suffix = executable;
        this.aliases = aliases;
    }

    public boolean isValidAlias(String alias) {
        Preconditions.checkNotNull(alias);
        for (final String s : this.aliases)
            if (s.equalsIgnoreCase(alias)) return true;

        return false;
    }

    public static OS forAlias(String alias) {
        Preconditions.checkNotNull(alias);
        for (final OS os : OS.values())
            if (os.isValidAlias(alias)) return os;

        return null;
    }

    /**
     * Get this System OS
     *
     * @return
     */
    public static OS get() {
        final String name = System.getProperty("os.name").toLowerCase();

        if (name.contains("win"))
            return WINDOWS;
        else if (name.contains("nix") || name.contains("nux") || name.contains("nax"))
            return UNIX;
        else
            return UNSUPPORTED;
    }

    /**
     * Get the intended Architecture of the JVM. This is not
     * necessarily the Computer's architecture. <br />
     * A 32-bit JVM can be run on a 64-bit Computer. <br />
     * <br />
     * To get the Computer Architecture, use {@link #getArch()}.
     *
     * @return JVM Architecture (x86/x64)
     */
    public static Bit getArchJVM() {
        final String arch = System.getProperty("os.arch");

        if (!arch.contains("64"))
            return Bit.BIT_32;
        else
            return Bit.BIT_64;
    }

    /**
     * Get Architecture of the computer. This determines the ACTUAL
     * Bitness of the computer <br />
     * this means that the COMPUTER may be 64 bit, but may be running
     * a 32 bit JVM. To get <br />
     * the JVM Bitness, use {@link #getArchJVM()}.
     *
     * @return Actual Architecture of the Computer (x86 or x64)
     */
    public static Bit getArch() {
        final String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        final String wow64 = System.getenv("PROCESSOR_ARCHITEW6432");

        if (arch == null)
            return getArchJVM();

        if (arch.contains("64") || ((wow64 != null) && wow64.contains("64")))
            return Bit.BIT_64;
        else
            return Bit.BIT_32;
    }

    public static int processorCount() {
        return rt.availableProcessors();
    }

    public static long getTotalMemory(MemoryUnit units) {
        return units.convert(rt.totalMemory(), MemoryUnit.BYTES);
    }

    public static long getUsedMemory(MemoryUnit units) {
        return units.convert(rt.totalMemory() - rt.freeMemory(), MemoryUnit.BYTES);
    }

    public static long getFreeMemory(MemoryUnit units) {
        return units.convert(rt.freeMemory(), MemoryUnit.BYTES);
    }

    public static double getUsedMemoryToTotalMemory(MemoryUnit units) {
        final double used = getUsedMemory(units);
        final double total = getTotalMemory(units);

        return used / total;
    }

    public static ObjectSet<OS> values() {
        return values;
    }

    public String name() {
        return nameLookup.get(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s]", name(), getArch().getTag(), getArch().getMnemonic());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
