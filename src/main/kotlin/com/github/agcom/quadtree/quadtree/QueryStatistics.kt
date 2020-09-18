package com.github.agcom.quadtree.quadtree

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
interface QueryStatistics {

    fun `points++`()

    fun `quadTrees++`()

    fun setTime(time: Duration)

    fun reset()

    fun done()

    fun points(): Int
    fun quadTrees(): Int
    fun time(): Duration

}