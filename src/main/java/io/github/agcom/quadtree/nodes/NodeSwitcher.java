package io.github.agcom.quadtree.nodes;

import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.util.List;

import static javafx.beans.binding.Bindings.valueAt;

//FIXME: Default property not working
public class NodeSwitcher extends BorderPane {

    //TODO: add support for remove and ...
    private ObservableList<Node> nodes = FXCollections.observableArrayList();
    private IntegerProperty currentNodeIndex = new SimpleIntegerProperty(this, "currentNodeIndex", -1);
    private ObjectBinding<Node> currentNodeBinding;

    public NodeSwitcher(@NamedArg(value = "nodes") List<Node> nodes, @NamedArg(value = "startNodeIndex") int startNodeIndex) {

        if(nodes != null) this.nodes.addAll(nodes);

        currentNodeBinding = Bindings.createObjectBinding(() -> {

            if(getCurrentNodeIndex() == -1 || this.nodes.isEmpty()) return null;
            return this.nodes.get(getCurrentNodeIndex());

        }, currentNodeIndex);

        currentNodeBinding.addListener(observable -> {

            Node node = currentNodeBinding.get();

            if(node == null) super.setCenter(null);
            else super.setCenter(node);

        });

        setCurrentNodeIndex(startNodeIndex);

    }

    public NodeSwitcher() {

        this(null, -1);

    }

    public ObservableList<Node> getNodes() {
        return nodes;
    }

    public int getCurrentNodeIndex() {
        return currentNodeIndex.get();
    }

    public IntegerProperty currentNodeIndexProperty() {
        return currentNodeIndex;
    }

    public void setCurrentNodeIndex(int currentNodeIndex) {
        this.currentNodeIndex.set(currentNodeIndex);
    }

}
