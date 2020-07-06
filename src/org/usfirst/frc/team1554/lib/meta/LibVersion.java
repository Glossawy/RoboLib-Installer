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


package org.usfirst.frc.team1554.lib.meta;

public final class LibVersion {

    private LibVersion() {
    }

    public static final String NAME = "RoboLib";
    public static final String VERSION = "1.0.0";

    public static final int MAJOR, MINOR, REVISION;

    static {
        try {
            final String[] parts = VERSION.split("\\.");
            MAJOR = parts.length < 1 ? 0 : Integer.parseInt(parts[0]);
            MINOR = parts.length < 2 ? 0 : Integer.parseInt(parts[1]);
            REVISION = parts.length < 3 ? 0 : Integer.parseInt(parts[2]);
        } catch (final Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public static boolean isEqual(int major, int minor, int revision) {
        return (MAJOR == major) && (MINOR == minor) && (REVISION == revision);
    }

    public static boolean isHigher(int major, int minor, int revision) {
        return isHigherOrEqualTo(major, minor, revision + 1);
    }

    public static boolean isHigherOrEqualTo(int major, int minor, int revision) {
        if (MAJOR != major)
            return major < MAJOR;
        else if (MINOR != minor) return minor < MINOR;

        return REVISION >= revision;
    }

    public static boolean isLower(int major, int minor, int revision) {
        return isLowerOrEqualTo(major, minor, revision - 1);
    }

    public static boolean isLowerOrEqualTo(int major, int minor, int revision) {
        if (MAJOR != major)
            return major > MAJOR;
        else if (MINOR != minor) return minor > MINOR;

        return REVISION <= revision;
    }

}
