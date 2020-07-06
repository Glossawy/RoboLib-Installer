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

/**
 * A convenience class for handling preconditions. All will throw
 * RuntimeException's if the precondition is not met. Inspired by
 * Google Guava Preconditions.
 *
 * @author Glossawy
 */
public final class Preconditions {

    /**
     * Ensure an Expression is true or thorw an IllegalArgumentException.
     *
     * @param exp
     */
    public static void checkExpression(boolean exp) {
        if (!exp) throw new IllegalArgumentException();
    }

    /**
     * Ensure an Expression is true or throw an IllegalArgumentException
     * with the given errorMessage.
     *
     * @param exp
     * @param errorMessage
     */
    public static void checkExpression(boolean exp, String errorMessage) {
        if (!exp) throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Ensure an Expression is true or throw an IllegalArgumentException
     * with the given, formatted, message.
     *
     * @param exp
     * @param format
     * @param messageItems
     */
    public static void checkExpression(boolean exp, String format, Object... messageItems) {
        if (!exp)
            throw new IllegalArgumentException(String.format(format, toStringArray(messageItems)));
    }

    /**
     * Ensure an Expression is true or throw an IllegalStateException.
     *
     * @param exp
     */
    public static void checkState(boolean exp) {
        if (!exp) throw new IllegalStateException();
    }

    /**
     * Ensure an Expression is true or throw an IllegalStateException
     * with the given errorMessage.
     *
     * @param exp
     * @param errorMessage
     */
    public static void checkState(boolean exp, String errorMessage) {
        if (!exp) throw new IllegalStateException(errorMessage);
    }

    /**
     * Ensure an Expression is true or throw an IllegalStateException
     * with the given, formatted, message.
     *
     * @param exp
     * @param format
     * @param messageItems
     */
    public static void checkState(boolean exp, String format, Object... messageItems) {
        if (!exp)
            throw new IllegalStateException(String.format(format, toStringArray(messageItems)));
    }

    /**
     * Ensure a given Reference is Not Null or throw a NPE.
     *
     * @param ref
     * @return
     */
    public static <E> E checkNotNull(E ref) {
        if (ref == null) throw new NullPointerException();

        return ref;
    }

    /**
     * Ensure a given Reference is Not Null or throw a NPE with the
     * given message.
     *
     * @param ref
     * @param errorMessage
     * @return
     */
    public static <E> E checkNotNull(E ref, String errorMessage) {
        if (ref == null) throw new NullPointerException(errorMessage);

        return ref;
    }

    /**
     * Ensure a given Reference is Not Null or throw a NPE with the
     * given, formatted, message.
     *
     * @param ref
     * @param format
     * @param messageItems
     * @return
     */
    public static <E> E checkNotNull(E ref, String format, Object... messageItems) {
        if (ref == null)
            throw new NullPointerException(String.format(format, toStringArray(messageItems)));

        return ref;
    }

    /**
     * Ensure that an index is in the range [0, size) or throw an
     * IndexOutOfBoundsException
     *
     * @param index
     * @param size
     */
    public static void checkElementIndex(int index, int size) {
        checkElementIndex(index, size, "index");
    }

    /**
     * Ensure that an index is in the range [0, size) or throw an
     * IndexOutOfBoundsException with the given description.
     *
     * @param index
     * @param size
     * @param desc
     * @return
     */
    public static int checkElementIndex(int index, int size, String desc) {
        if ((index < 0) || (index >= size))
            throw new IndexOutOfBoundsException(badElementMessage(index, size, desc));

        return index;
    }

    private static String badElementMessage(int index, int size, String desc) {
        if (index < 0)
            return String.format("%s -- The index (%s) must not be negative! (Size = %s)", desc, index, size);
        else if (index >= size)
            return String.format("%s -- (%s) must be between 0 and (%s)!", desc, index, size);
        else
            return String.format("Size Cannot Be Negative! (size = %s)", size);
    }

    private static Object[] toStringArray(Object[] arr) {
        final String[] tmp = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            tmp[i] = String.valueOf(arr[i]);
        }

        return tmp;
    }

}
