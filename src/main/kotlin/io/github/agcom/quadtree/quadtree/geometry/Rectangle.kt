package io.github.agcom.quadtree.quadtree.geometry

data class Rectangle(var x: Int, var y: Int, val width: Int, val height: Int) : Area {

    constructor(width: Int, height: Int) : this(0, 0, width, height)

    override operator fun contains(p: Point) = p.x >= x
            && p.y >= y
            && p.x <= x + width
            && p.y <= y + height

    override fun overlaps(rect: Rectangle) = !(x + width <= rect.x
            || y + height <= rect.y
            || x >= rect.x + rect.width
            || y >= rect.y + rect.height)

}