package com.github.agcom.quadtree.nodes;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class LabeledSeparator extends HBox {

    private StringProperty text = new SimpleStringProperty(this, "text");

    public LabeledSeparator(@NamedArg(value = "text") String text) {

        getStyleClass().add("labeled-separator");

        setText(text);
        initView();

    }

    private void initView() {

        setSpacing(8);
        setAlignment(Pos.CENTER);

        Separator left = new Separator();
        Separator right = new Separator();
        Label label = new Label();
        label.setTextFill(Color.web("#757575"));
        label.textProperty().bind(text);

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        getChildren().addAll(left, label, right);

    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }
}
