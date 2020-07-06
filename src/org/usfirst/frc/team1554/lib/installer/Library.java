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

package org.usfirst.frc.team1554.lib.installer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

public class Library {

    private final Optional<Path> fileLocation;
    private final Optional<Path> srcLocation;
    private final boolean required;

    public Library(String filename, Path projectDir, boolean isRequired) {
        int dIdx = filename.lastIndexOf('.');
        String srcName = filename.substring(0, dIdx) + "-sources" + filename.substring(dIdx);

        fileLocation = locateLibFile(filename, projectDir);

        if (fileLocation.isPresent()) {
            srcLocation = locateLibFile(srcName, projectDir);
        } else if (!isRequired) {
            srcLocation = Optional.empty();
        } else
            throw new MissingRequirementException("Library '" + filename + "' could not be found! But is marked as required!");

        required = isRequired;
    }

    public Path getLibraryFile() {
        return fileLocation.get();
    }

    public Path getSourceFile() {
        return srcLocation.get();
    }

    public boolean isAvailable() {
        return fileLocation.isPresent();
    }

    public boolean hasSourceFile() {
        return srcLocation.isPresent();
    }

    public boolean isRequired() {
        return required;
    }

    private static Optional<Path> locateLibFile(String filename, Path root) {
        final ObjectProperty<Path> lib = new SimpleObjectProperty<>();
        try {
            Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), 10, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().equals(filename)) {
                        lib.set(file);
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if (file.getFileName().toString().equals(filename)) {
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IORuntimeException("Error Walking File Tree while looking for " + filename + "!", e);
        }

        if (lib.get() == null)
            return Optional.empty();

        return Optional.of(lib.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return Objects.equals(fileLocation, library.fileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileLocation);
    }

}
