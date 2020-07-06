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


package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utilities for Instantiating and Manipulating Maps
 *
 * @author Glossawy
 */
public class Maps {

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int estimation) {
        return new HashMap<>(estimation);
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
        Preconditions.checkNotNull(map);
        return new HashMap<>(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap<>(type);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(EnumMap<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map) {
        return new TreeMap<>(map);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }
}
