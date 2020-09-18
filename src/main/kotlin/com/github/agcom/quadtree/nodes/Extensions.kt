package com.github.agcom.quadtree.nodes

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import com.github.agcom.quadtree.quadtree.QuadTree
import com.github.agcom.quadtree.quadtree.geometry.Circle
import com.github.agcom.quadtree.quadtree.geometry.Point
import com.github.agcom.quadtree.quadtree.geometry.Rectangle
import kotlin.time.ExperimentalTime

fun Point.draw(canvas: Canvas, radius: Double = 4.0, paint: Paint = Color.BLACK) {

    canvas.graphicsContext2D.apply {

        save()

        fill = paint
        fillOval(x - radius, y - radius, radius * 2, radius * 2)

        restore()

    }

}

fun Canvas.clear() = graphicsContext2D.clearRect(0.0, 0.0, width, height)

fun Rectangle.draw(canvas: Canvas, strokeWidth: Double = 1.0, strokePaint: Paint = Color.BLACK) {

    canvas.graphicsContext2D.apply {

        save()

        stroke = strokePaint
        lineWidth = strokeWidth
        strokeRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

        restore()

    }

}

@ExperimentalTime
fun QuadTree.draw(canvas: Canvas, strokeWidth: Double = 1.0, pointRadius: Double = 4.0, strokePaint: Paint = Color.BLACK, pointPaint: Paint = Color.BLACK) {

    canvas.graphicsContext2D.apply {

        save()

        boundary.draw(canvas, strokeWidth, strokePaint)

        points.forEach { it.draw(canvas, pointRadius, pointPaint) }

        if(isDivided) {

            topLeft!!.draw(canvas, strokeWidth, pointRadius, strokePaint, pointPaint)
            topRight!!.draw(canvas, strokeWidth, pointRadius, strokePaint, pointPaint)
            bottomLeft!!.draw(canvas, strokeWidth, pointRadius, strokePaint, pointPaint)
            bottomRight!!.draw(canvas, strokeWidth, pointRadius, strokePaint, pointPaint)

        }

        restore()

    }

}

fun Rectangle.getAreaShape(): AreaShape {

    val rectShape = javafx.scene.shape.Rectangle(width.toDouble(), height.toDouble(), null).run {

        stroke = Color.RED
        strokeWidth = 1.0
        this

    }

    return object : AreaShape(this, rectShape) {

        override fun move(x: Int, y: Int) {

            area as Rectangle

            area.x = x
            area.y = y

        }

    }

}

fun Circle.getAreaShape(): AreaShape {

    val cirShape = javafx.scene.shape.Circle(radius.toDouble(), null).run {

        stroke = Color.RED
        strokeWidth = 1.0
        this

    }

    return object : AreaShape(this, cirShape) {

        override fun move(x: Int, y: Int) {

            area as Circle

            area.xCenter = x + radius
            area.yCenter = y + radius

        }

    }

}