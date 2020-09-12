package quadtree.geometry

import utils.pow
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class Circle(var xCenter: Int = 0, var yCenter: Int = 0, val radius: Int) : Area {

    constructor(radius: Int) : this(0, 0, radius)

    override fun contains(p: Point): Boolean {

        return sqrt(((p.x - xCenter).pow(2) + (p.y - yCenter).pow(2)).toDouble()).toInt() <= radius

    }

    override fun overlaps(rect: Rectangle): Boolean {

        val circleDistanceX = abs(xCenter - rect.x - rect.width/2)
        val circleDistanceY = abs(yCenter - rect.y - rect.height/2)

        return when {

            circleDistanceX > (rect.width/2 + radius) -> false

            circleDistanceY > (rect.height/2 + radius) -> false

            circleDistanceX <= (rect.width/2) -> true

            circleDistanceY <= (rect.height/2) -> true

            else -> {

                val cornerDistanceSquare = (circleDistanceX - rect.width / 2).pow(2) + (circleDistanceY - rect.height / 2).pow(2)
                cornerDistanceSquare <= radius.pow(2)

            }

        }

    }

}