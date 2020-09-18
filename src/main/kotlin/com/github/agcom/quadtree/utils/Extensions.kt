package com.github.agcom.quadtree.utils

import com.github.agcom.quadtree.Launcher
import javafx.fxml.FXMLLoader
import java.io.InputStream
import java.net.URL
import kotlin.math.pow

fun Int.pow(power: Int): Int = toDouble().pow(power).toInt()

fun String.toUrl(): URL = Launcher::class.java.getResource(this)
fun String.toStream(): InputStream = Launcher::class.java.getResourceAsStream(this)

fun <T> loadFXML(fxmlPath: String): T = FXMLLoader.load<T>(fxmlPath.toUrl())

public infix fun <T> Sequence<T>.lazyPlus(otherGenerator: () -> Sequence<T>) =
    object : Sequence<T> {
        private val thisIterator: Iterator<T> by lazy { this@lazyPlus.iterator() }
        private val otherIterator: Iterator<T> by lazy { otherGenerator().iterator() }

        override fun iterator() = object : Iterator<T> {
            override fun next(): T =
                if (thisIterator.hasNext())
                    thisIterator.next()
                else
                    otherIterator.next()

            override fun hasNext(): Boolean =
                thisIterator.hasNext() || otherIterator.hasNext()
        }
    }