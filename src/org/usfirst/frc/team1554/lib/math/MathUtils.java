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

public class MathUtils {

    private static final Random rand = new RandomXS128(System.currentTimeMillis());

    /**
     * Random number between [0, upper]
     *
     * @param upper
     * @return
     */
    public static int random(int upper) {
        return rand.nextInt(upper + 1);
    }

    /**
     * Random number between [start, end]
     *
     * @param start
     * @param end
     * @return
     */
    public static int random(int start, int end) {
        return start + rand.nextInt((end - start) + 1);
    }

    /**
     * Random number between [0, range]
     *
     * @param range
     * @return
     */
    public static long random(long range) {
        return ((RandomXS128) rand).nextLong(range + 1);
    }

    /**
     * Random number between [start, end]
     *
     * @param start
     * @param end
     * @return
     */
    public static long random(long start, long end) {
        return start + random(end - start);
    }

    /**
     * Random boolean. True or False.
     *
     * @return
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * Random Boolean weighted to one side. 1 being true, 0 being
     * flase.
     *
     * @param chance
     * @return
     */
    public static boolean nextBoolean(double chance) {
        return random() < chance;
    }

    /**
     * Random decimal between [0, 1.0)
     *
     * @return
     */
    public static double random() {
        return rand.nextDouble();
    }

    /**
     * Random decimal between [0, range)
     *
     * @param range
     * @return
     */
    public static double random(double range) {
        return rand.nextDouble() * range;
    }

    /**
     * Random decimal between [start, end)
     *
     * @param start
     * @param end
     * @return
     */
    public static double random(double start, double end) {
        return start + (rand.nextDouble() * (end - start));
    }

    /**
     * Random Sign, i.e. -1 or 1.
     *
     * @return
     */
    public static int randomSign() {
        return 1 | (rand.nextInt() >> 31);
    }

    /**
     * Clamp Val between Max and Min
     */
    public static int clamp(int val, int max, int min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static long clamp(long val, long max, long min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static float clamp(float val, float max, float min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Linearly Interpolate between 'from' and 'to' at 'progress'
     * position.
     */
    public static double lerp(double from, double to, double progress) {
        return from + ((to - from) * progress);
    }

    public static int nextPowerOfTwo(int n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }

    public static double ln(double x) {
        return Math.log(x);
    }

    /**
     * Take the Logarithm of X in Base A.
     *
     * @param a
     * @param x
     * @return
     */
    public static double log(double a, double x) {
        return Math.log(x) / Math.log(a);
    }

    /**
     * Take the Logarithm of X in base 2.
     *
     * @param x
     * @return
     */
    public static double log2(double x) {
        return log(2, x);
    }

    public static double log10(double x) {
        return Math.log10(x);
    }

    public static int booleanToInt(boolean expression) {
        return expression ? 1 : 0;
    }

}
