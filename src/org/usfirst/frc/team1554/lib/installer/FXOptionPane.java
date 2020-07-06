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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class FXOptionPane extends Stage {

    public static final int MESSAGE_MIN_WIDTH = 180;
    public static final int MESSAGE_MAX_WIDTH = 800;
    public static final int BUTTON_WIDTH = 60;
    public static final int MARGIN = 10;

    @SuppressWarnings("ConstantConditions")
    private FXOptionPane(Builder builder) {
        super(StageStyle.UTILITY);

        if (builder.owner != null) {
            initOwner(builder.owner);
        }

        initModality(builder.modality);
        setTitle(builder.title);
        setResizable(builder.resizable);

        toFront();
        centerOnScreen();
        FXGuiUtils.makeAlwaysOnTop(this);

        double sceneHeight = builder.height == Double.NaN ? -1 : builder.height;
        double sceneWidth = builder.width == Double.NaN ? -1 : builder.width;

        setScene(new Scene(builder.root, sceneWidth, sceneHeight));
        if (builder.blocking) {
            showAndWait();
        } else {
            show();
        }
    }

    public static Builder builder() {
        return Builder.create("");
    }

    public static Builder builder(String message) {
        return Builder.create(message);
    }

    public static void showBlockingMessage(Window owner, String title, String message, IconType icon) {
        Builder.create(message).setImage(icon).makeOkButton(DEFAULT_CLOSE_ACTION).setOwner(owner).setModality(Modality.APPLICATION_MODAL).setTitle(title == null ? GUIRef.TITLE + " Message" : title).makeBlocking().build();
    }

    public static void showBlockingMessage(String title, String message, IconType icon) {
        showBlockingMessage(null, title, message, icon);
    }

    public static void showBlockingMessage(String title, String message) {
        showBlockingMessage(null, title, message, IconType.INFO);
    }

    public static void showBlockingMessage(String message) {
        Builder.create(message).toInfo().makeOkButton(DEFAULT_CLOSE_ACTION).setModality(Modality.APPLICATION_MODAL).makeBlocking().build();
    }

    public static void showMessage(Window owner, String title, String message, IconType icon) {
        Builder.create(message).setImage(icon).makeOkButton(DEFAULT_CLOSE_ACTION).setOwner(owner).setModality(Modality.APPLICATION_MODAL).setTitle(title == null ? GUIRef.TITLE + " Message" : title).build();
    }

    public static void showMessage(String title, String message, IconType icon) {
        showMessage(null, title, message, icon);
    }

    public static void showMessage(String title, String message) {
        showMessage(null, title, message, IconType.INFO);
    }

    public static void showMessage(String message) {
        Builder.create(message).toInfo().makeOkButton(DEFAULT_CLOSE_ACTION).setModality(Modality.APPLICATION_MODAL).build();
    }

    public static boolean showConfirmation(Window owner, String title, String message, OptionType options, IconType icon) {
        final AtomicBoolean confirmed = new AtomicBoolean(false);
        final Builder builder = Builder.create(message).setTitle(title).setImage(icon).setOwner(null).makeBlocking().setModality(Modality.APPLICATION_MODAL);

        options.install(builder, confirmed);
        builder.build();
        return confirmed.get();
    }

    public static boolean showConfirmation(String title, String message, OptionType options, IconType icon) {
        return showConfirmation(null, title, message, options, icon);
    }

    public static boolean showConfirmation(String title, String message, OptionType options) {
        return showConfirmation(title, message, options, IconType.CONFIRM);
    }

    public static boolean showConfirmation(String message, OptionType options) {
        return showConfirmation(GUIRef.TITLE + " Prompt", message, options);
    }

    public static class Builder {

        private Window owner = null;
        private Modality modality = Modality.APPLICATION_MODAL;
        private String title = GUIRef.TITLE + " Prompt";

        private final BorderPane root = new BorderPane();
        private final ImageView icon = new ImageView();

        private final HBox msgBox = new HBox();
        private Button accept;
        private Button reject;
        private final Label label;

        private final HBox btnBox = new HBox();

        private boolean resizable = true;
        private boolean blocking = false;

        private double height = Double.NaN;
        private double width = Double.NaN;

        private Builder(String message) {
            this.label = new Label(message);
            this.label.setWrapText(false);
            this.label.setMinWidth(MESSAGE_MIN_WIDTH);
            this.label.setMaxWidth(MESSAGE_MAX_WIDTH);

            toInfo();

            this.msgBox.setAlignment(Pos.CENTER_LEFT);
            this.msgBox.getChildren().add(this.label);

            this.btnBox.setSpacing(MARGIN);
            this.btnBox.setAlignment(Pos.BOTTOM_CENTER);

            BorderPane.setAlignment(this.msgBox, Pos.CENTER);
            BorderPane.setMargin(this.msgBox, new Insets(MARGIN, MARGIN, MARGIN, 2 * MARGIN));
            BorderPane.setMargin(this.btnBox, new Insets(0, 0, 1.5 * MARGIN, 0));
            BorderPane.setMargin(this.icon, new Insets(MARGIN));

            this.root.setLeft(this.icon);
            this.root.setCenter(this.msgBox);
            this.root.setBottom(this.btnBox);

            this.label.setId("optionpane-label");
            this.icon.setId("optionpane-icon");

            this.icon.setEffect(new DropShadow(this.icon.getImage().getWidth(), Color.LIGHTGRAY));
        }

        public static Builder create(String message) {
            return new Builder(message);
        }

        public Builder setResizable(boolean resize) {
            this.resizable = resize;

            return this;
        }

        public Builder setOwner(Window owner) {
            this.owner = owner;

            return this;
        }

        public Builder setModality(Modality modality) {
            this.modality = modality;

            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;

            return this;
        }

        public Builder setMessage(String message) {
            this.label.setText(message);

            return this;
        }

        public Builder setImage(Image ico) {
            this.icon.setImage(ico);

            return this;
        }

        public Builder setImageFromResource(String resource) throws IOException {
            return setImage(new Image(FXGuiUtils.loadImage(resource).stream()));
        }

        public Builder setImage(IconType ico) {
            try {
                return setImageFromResource(ico.imageName);
            } catch (IOException e) {
                new IOException("Failed to set Icon from IconType! Must be out-of-date or misplaced images? Check package name for resources.", e).printStackTrace();
            }

            return this;
        }

        public Builder toWarning() {
            return setImage(IconType.WARNING);
        }

        public Builder toError() {
            return setImage(IconType.ERROR);
        }

        public Builder toInfo() {
            return setImage(IconType.INFO);
        }

        public Builder toConfirmation() {
            return setImage(IconType.CONFIRM);
        }

        public Builder makeBlocking() {
            this.blocking = true;

            return this;
        }

        public Builder makeOkButton(EventHandler<ActionEvent> onClick) {
            this.accept = createButton("Ok", onClick);

            return this;
        }

        public Builder makeCancelButton(EventHandler<ActionEvent> onClick) {
            this.reject = createButton("Cancel", onClick);

            return this;
        }

        public Builder makeYesButton(EventHandler<ActionEvent> onClick) {
            this.accept = createButton("Yes", onClick);

            return this;
        }

        public Builder makeNoButton(EventHandler<ActionEvent> onClick) {
            this.reject = createButton("No", onClick);

            return this;
        }

        public Builder setWidth(double width) {
            this.width = width;

            return this;
        }

        public Builder setHeight(double height) {
            this.height = height;
            return this;
        }

        public void build() {

            if ((this.accept == null) && (this.reject == null))
                throw new IllegalStateException("An FXOptionPane MUST have a Button!");

            if (this.accept != null) {
                this.btnBox.getChildren().add(this.accept);
                this.accept.setId("optionpane-button");
            }

            if (this.reject != null) {
                this.btnBox.getChildren().add(this.reject);
                this.reject.setId("optionpane-button");
            }

            final Runnable r = () -> new FXOptionPane(this);

            if (Platform.isFxApplicationThread()) {
                r.run();
            } else {
                Platform.runLater(r);
            }

        }

        private Button createButton(String lbl, EventHandler<ActionEvent> onClick) {
            final Button b = new Button(lbl);
            b.setPrefWidth(BUTTON_WIDTH);
            b.setOnAction(onClick);

            return b;
        }
    }

    public enum IconType {
        INFO("infoIcon.png"), WARNING("warningIcon.png"), ERROR("errorIcon.png"), CONFIRM("confirmationIcon.png");

        public final String imageName;

        IconType(String imageName) {
            this.imageName = imageName;
        }
    }

    public enum OptionType {
        OK {
            @Override
            public void install(Builder builder, final AtomicBoolean state) {
                builder.makeOkButton(evt -> {
                    state.set(true);
                    ((Button) evt.getSource()).getScene().getWindow().hide();
                });
            }
        },
        YES_NO {
            @Override
            public void install(Builder builder, final AtomicBoolean state) {
                builder.makeYesButton(evt -> {
                    state.set(true);
                    ((Button) evt.getSource()).getScene().getWindow().hide();
                });

                builder.makeNoButton(evt -> {
                    state.set(false);
                    ((Button) evt.getSource()).getScene().getWindow().hide();
                });
            }
        },
        OK_CANCEL {
            @Override
            public void install(Builder builder, final AtomicBoolean state) {
                builder.makeOkButton(evt -> {
                    state.set(true);
                    ((Button) evt.getSource()).getScene().getWindow().hide();
                });

                builder.makeCancelButton(evt -> {
                    state.set(false);
                    ((Button) evt.getSource()).getScene().getWindow().hide();
                });
            }
        };

        public void install(Builder builder, final AtomicBoolean stateSwitch) {
            throw new AbstractMethodError();
        }
    }

    static final EventHandler<ActionEvent> DEFAULT_CLOSE_ACTION = event -> ((Node) event.getSource()).getScene().getWindow().hide();

}
