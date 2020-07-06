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

import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class FXGuiUtils {

    public static void makeAlwaysOnTop(final Stage stage) {
        try {
            stage.setAlwaysOnTop(true);
            return;
        } catch (Throwable t) {
            // Ignore, We must be using an older JavaFX Version
        }

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                stage.requestFocus();
                stage.toFront();
            }
        });
    }

    public static Resource loadImage(String name) {
        return () -> FXGuiUtils.class.getClassLoader().getResource(GUIRef.RES_PACKAGE + name);
    }

    @FunctionalInterface
    interface Resource {
        URL url() throws MalformedURLException;

        default InputStream stream() throws IOException {
            URL url = url();
            return url == null ? null : url.openStream();
        }

    }

}
