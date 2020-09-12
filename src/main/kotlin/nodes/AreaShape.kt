package nodes

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.shape.Shape
import quadtree.geometry.Area

abstract class AreaShape(val area: Area, val node: Node) {

    abstract fun move(x: Int, y: Int)

}