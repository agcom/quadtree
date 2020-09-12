package quadtree.geometry

interface Area {

    operator fun contains(p: Point): Boolean

    fun overlaps(rect: Rectangle): Boolean

}