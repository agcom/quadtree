package io.github.agcom.quadtree.nodes

import javafx.scene.Node
import io.github.agcom.quadtree.quadtree.geometry.Area

abstract class AreaShape(val area: Area, val node: Node) {

    abstract fun move(x: Int, y: Int)

}