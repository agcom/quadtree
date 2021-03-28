package io.github.agcom.quadtree.nodes;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class FakeFocus extends TextField {

    public FakeFocus() {

        setPrefSize(0, 0);
        setMaxSize(0, 0);
        setOpacity(0);
        setManaged(false);
        setTextFormatter(new TextFormatter<>(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return "";
            }

            @Override
            public String fromString(String string) {
                return "";
            }
        }));

    }

}
