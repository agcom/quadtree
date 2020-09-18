package com.github.agcom.quadtree.utils

import java.util.concurrent.Executors

val executor = Executors.newScheduledThreadPool(4) { Thread(it).run { isDaemon = true ;this } }
val quadTreeModif = Executors.newSingleThreadExecutor() { Thread(it).run { isDaemon = true; this } }
val timer = Executors.newSingleThreadScheduledExecutor { Thread(it).run { isDaemon = true ;this } }