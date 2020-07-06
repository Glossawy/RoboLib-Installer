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


package org.usfirst.frc.team1554.lib.math;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

// Xor Shift 128 bit PRNG
public class RandomXS128 extends Random {

    private static final long serialVersionUID = 1087515677630603741L;

    private static final AtomicLong uniqueLong = new AtomicLong(8_682_522_807_148_012L);
    private static final double NORMALIZER_64 = 1.0 / (1L << 53);
    private static final double NORMALIZER_32 = 1.0 / (1L << 24);

    private long seed1, seed2;

    public RandomXS128() {
        long next;
        for (; ; ) {
            final long current = uniqueLong.get();
            next = current * 181_783_497_276_652_981L;
            if (uniqueLong.compareAndSet(current, next)) {
                break;
            }
        }

        setSeed(next ^ System.nanoTime());
    }

    public RandomXS128(long seed) {
        setSeed(seed);
    }

    @Override
    public long nextLong() {
        long s2 = this.seed1;
        final long s1 = this.seed2;

        this.seed1 = s1;
        s2 ^= s2 << 23;
        return (this.seed2 = s2 ^ s1 ^ (s2 >> 17) ^ (s1 >>> 26)) + s1;
    }

    /**
     * Returns Random Long
     *
     * @param n
     * @return
     */
    public long nextLong(final long n) {
        if (n <= 0)
            throw new IllegalArgumentException("N must be > 0");

        while (true) {
            final long bits = nextLong() >>> 1;
            final long value = bits % n;
            if ((((bits - value) + n) - 1) >= 0) return value;
        }
    }

    @Override
    protected int next(int bits) {
        return (int) (nextLong() & ((1L << bits) - 1));
    }

    /**
     * Returns Random Int. <br />
     * <Br />
     * Uses {@link #nextLong()}
     */
    @Override
    public int nextInt() {
        return (int) nextLong();
    }

    /**
     * Returns a Random Int from [0, n)
     */
    @Override
    public int nextInt(final int n) {
        return (int) nextLong(n);
    }

    @Override
    public void setSeed(final long seed) {
        final long propSeed = hash(seed == 0 ? Long.MIN_VALUE : seed);
        setState(propSeed, hash(propSeed));
    }

    /**
     * Random double between [0.0, 1.0)
     */
    @Override
    public double nextDouble() {
        return (nextLong() >>> 11) * NORMALIZER_64;
    }

    /**
     * Ranbdom float between [0.0, 1.0)
     */
    @Override
    public float nextFloat() {
        return (float) ((nextLong() >>> 40) * NORMALIZER_32);
    }

    @Override
    public boolean nextBoolean() {
        return (nextLong() & 1) != 0;
    }

    @Override
    public void nextBytes(final byte[] bytes) {
        int n;
        int l = bytes.length;

        while (l != 0) {
            n = l < 8 ? l : 8;
            for (long bits = nextLong(); n-- != 0; bits >>= 8) {
                bytes[--l] = (byte) bits;
            }
        }
    }

    public void setState(final long seed1, final long seed2) {
        this.seed1 = seed1;
        this.seed2 = seed2;
    }

    public long getState(boolean firstSeed) {
        return firstSeed ? this.seed1 : this.seed2;
    }

    private static long hash(long x) {
        // murmur hash 3
        x ^= x >>> 33;
        x *= 0xff51afd7ed558ccdL;
        x ^= x >>> 33;
        x *= 0xc4ceb9fe1a85ec53L;
        x ^= x >>> 33;

        return x;
    }

}
