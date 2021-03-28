package io.github.agcom.quadtree.quadtree

import io.reactivex.*
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import io.github.agcom.quadtree.quadtree.geometry.Area
import io.github.agcom.quadtree.quadtree.geometry.Point
import io.github.agcom.quadtree.quadtree.geometry.Rectangle
import io.github.agcom.quadtree.utils.lazyPlus
import java.lang.RuntimeException
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class QuadTree(val boundary: Rectangle, private val capacity: Int = 4, val parent: QuadTree? = null) : Iterable<Point> {

    val points = arrayListOf<Point>()
    var isDivided = false
    private set

    var topLeft: QuadTree? = null
    private set
    var topRight: QuadTree? = null
    private set
    var bottomLeft: QuadTree? = null
    private set
    var bottomRight: QuadTree? = null
    private set

    fun insert(p: Point): Boolean {

        if (p !in boundary) return false

        if (isDivided) return insertIntoChild(p)

        if (points.size < capacity) {

            points.add(p)
            return true

        }

        divide()
        return insertIntoChild(p)

    }

    fun delete(p: Point): Boolean {

        if(p !in boundary) return false

        if(points.contains(p)) {

            points.remove(p)

            if(!isDivided) return true

            return when {

                topLeft!!.points.size != 0 -> {

                    points.add(topLeft!!.points[0])
                    topLeft!!.delete(topLeft!!.points[0])

                }

                topRight!!.points.size != 0 -> {

                    points.add(topRight!!.points[0])
                    topRight!!.delete(topRight!!.points[0])

                }

                bottomLeft!!.points.size != 0 -> {

                    points.add(bottomLeft!!.points[0])
                    bottomLeft!!.delete(bottomLeft!!.points[0])

                }

                bottomRight!!.points.size != 0 -> {

                    points.add(bottomRight!!.points[0])
                    bottomRight!!.delete(bottomRight!!.points[0])

                }

                else -> {

                    isDivided = false

                    topLeft = null
                    topRight = null
                    bottomLeft = null
                    bottomRight = null

                    true

                }

            }

        } else {

            if(!isDivided) return false

            return topLeft!!.delete(p) || topRight!!.delete(p) || bottomLeft!!.delete(p) || bottomRight!!.delete(p)

        }

    }

    private inline fun divide() {

        if (isDivided) throw RuntimeException("already divided")

        topLeft = QuadTree(Rectangle(boundary.x, boundary.y, boundary.width / 2, boundary.height / 2), capacity, this)
        topRight = QuadTree(Rectangle(boundary.x + boundary.width / 2, boundary.y, boundary.width / 2, boundary.height / 2), capacity, this)
        bottomLeft = QuadTree(Rectangle(boundary.x, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2), capacity, this)
        bottomRight = QuadTree(Rectangle(boundary.x + boundary.width / 2, boundary.y + boundary.height / 2, boundary.width / 2, boundary.height / 2), capacity, this)
        isDivided = true

    }
    private inline fun insertIntoChild(p: Point) = topLeft!!.insert(p) || topRight!!.insert(p) || bottomLeft!!.insert(p) || bottomRight!!.insert(p)

    @ExperimentalTime
    fun query(area: Area, queryStatistics: QueryStatistics? = null): Flowable<Point> {

        return Flowable.create<Point>({

            try {

                val time = measureTime { query(area, it, queryStatistics) }

                queryStatistics?.setTime(time)

            } finally {

                it.onComplete()
                queryStatistics?.done()

            }


        }, BackpressureStrategy.BUFFER)
            .onTerminateDetach()
            .subscribeOn(Schedulers.computation())
            .observeOn(JavaFxScheduler.platform())
    }

    private fun query(area: Area, emitter: Emitter<Point>, queryStatistics: QueryStatistics?) {

        queryStatistics?.`quadTrees++`()

        if(!area.overlaps(boundary)) return

        points.forEach {

            queryStatistics?.`points++`()

            if (area.contains(it)) emitter.onNext(it)

        }

        if(!isDivided) return

        topLeft!!.query(area, emitter, queryStatistics)
        topRight!!.query(area, emitter, queryStatistics)
        bottomLeft!!.query(area, emitter, queryStatistics)
        bottomRight!!.query(area, emitter, queryStatistics)

    }

    fun clear() {

        isDivided = false
        points.clear()
        topLeft = null
        topRight = null
        bottomLeft = null
        bottomRight = null

    }

    override fun iterator() = traversal().iterator()

    private fun traversal(): Sequence<Point> {

        var seq = points.asSequence()

        if(isDivided) {

            seq = seq.lazyPlus { topLeft!!.traversal() }
            seq = seq.lazyPlus { topRight!!.traversal() }
            seq = seq.lazyPlus { bottomLeft!!.traversal() }
            seq = seq.lazyPlus { bottomRight!!.traversal() }

        }

        return seq

    }



}