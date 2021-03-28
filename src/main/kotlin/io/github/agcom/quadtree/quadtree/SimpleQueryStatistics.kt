package io.github.agcom.quadtree.quadtree

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
abstract class SimpleQueryStatistics : QueryStatistics {

    var points: Int = 0
    private set

    var quadTrees: Int = 0
    private set

    var time: Duration = Duration.ZERO
    private set

    override fun `points++`() {
        points++
    }

    override fun `quadTrees++`() {
        quadTrees++
    }

    override fun setTime(time: Duration) {

        this.time = time

    }

    override fun points(): Int = points

    override fun quadTrees(): Int = quadTrees

    override fun time(): Duration = time

    override fun reset() {

        points = 0
        quadTrees = 0
        time = Duration.ZERO

    }

}