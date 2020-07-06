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

import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Needs Documentation
 *
 * @author Glossawy
 *         Created 3/8/2015 at 9:16 PM
 */
public final class ProgressDisplay extends Scene {

    private final StringProperty info;
    private final DoubleProperty progress;

    private ProgressDisplay(Parent root, double width, double height) {
        super(root, width, height);

        VBox rootPane = (VBox) root;
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setSpacing(2);

        HBox textDisplay = new HBox();
        HBox progressDisplay = new HBox();

        Label infoLabel = new Label("Installing...");
        infoLabel.setFont(Font.font("SansSerif", FontWeight.NORMAL, FontPosture.ITALIC, 10));
        infoLabel.setPadding(new Insets(0, 0, 0, 7));
        info = infoLabel.textProperty();


        textDisplay.getChildren().add(infoLabel);
        textDisplay.setAlignment(Pos.CENTER_LEFT);
        textDisplay.setSpacing(10);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPadding(new Insets(0, 5, 0, 5));
        progressBar.setPrefWidth(this.getWidth());
        this.progress = progressBar.progressProperty();

        progressDisplay.getChildren().addAll(progressBar);
        progressDisplay.setAlignment(Pos.BASELINE_LEFT);
        progressDisplay.setSpacing(10);

        rootPane.getChildren().addAll(textDisplay, progressDisplay);
    }

    public ProgressDisplay(@NamedArg("width") double width, @NamedArg("height") double height) {
        this(new VBox(), width, height);
    }

    public void setInfoText(String text) {
        this.info.set(text);
    }

    public String getInfoText() {
        return this.info.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress / 100.);

    }

    public double getProgress() {
        return this.progress.get() * 100.;
    }
}
