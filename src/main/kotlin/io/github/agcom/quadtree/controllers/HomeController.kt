package io.github.agcom.quadtree.controllers

import com.jfoenix.controls.JFXCheckBox
import com.jfoenix.controls.JFXRadioButton
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TextArea
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import io.github.agcom.quadtree.nodes.IntField
import io.github.agcom.quadtree.nodes.NodeSwitcher
import io.github.agcom.quadtree.nodes.getAreaShape
import io.github.agcom.quadtree.quadtree.QuadTree
import io.github.agcom.quadtree.quadtree.SimpleQueryStatistics
import io.github.agcom.quadtree.quadtree.geometry.Circle
import io.github.agcom.quadtree.quadtree.geometry.Point
import io.github.agcom.quadtree.quadtree.geometry.Rectangle
import java.net.URL
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@ExperimentalTime
class HomeController : Initializable {

    @FXML private lateinit var quadtreeController: QuadtreeController
    @FXML private lateinit var controlPane: VBox
    @FXML private lateinit var logs: TextArea
    @FXML private lateinit var queryShapeGroup: ToggleGroup
    @FXML private lateinit var circleQuery: JFXRadioButton
    @FXML private lateinit var rectQuery: JFXRadioButton
    @FXML private lateinit var queryWidth: IntField
    @FXML private lateinit var queryHeight: IntField
    @FXML private lateinit var width: IntField
    @FXML private lateinit var height: IntField
    @FXML private lateinit var radius: IntField
    @FXML private lateinit var drawDelay: IntField
    @FXML private lateinit var queryDelay: IntField
    @FXML private lateinit var randomize: IntField
    @FXML private lateinit var rebuildDelay: IntField
    @FXML private lateinit var capacity: IntField
    @FXML private lateinit var queryOptionsSwitcher: NodeSwitcher
    @FXML private lateinit var eraser: JFXCheckBox

    private val queryStatistics = object : SimpleQueryStatistics() {

        override fun done() {

            Platform.runLater { printQueryStatistics() }

        }

    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        quadtreeController.also {

            it.queryStatistics.value = queryStatistics
            it.erase.bindBidirectional(eraser.selectedProperty())
            it.query.bind(Bindings.createObjectBinding(Callable {

                when (queryShapeGroup.selectedToggle.userData as String) {

                    "circle" -> {

                        val radiusText = radius.text

                        Circle(radiusText?.toIntOrNull() ?: 100).getAreaShape()

                    }

                    "rect" -> {

                        val widthText = queryWidth.text
                        val heightText = queryHeight.text

                        Rectangle(widthText?.toIntOrNull() ?: 200, heightText?.toIntOrNull() ?: 200).getAreaShape()

                    }

                    else -> null

                }

            }, radius.textProperty(), queryWidth.textProperty(), queryHeight.textProperty(), queryShapeGroup.selectedToggleProperty()))
            it.drawDelay.bind(Bindings.createLongBinding(Callable { drawDelay.text?.toLongOrNull() ?: 0 }, drawDelay.textProperty()))
            it.queryDelay.bind(Bindings.createLongBinding(Callable { queryDelay.text?.toLongOrNull() ?: 10 }, queryDelay.textProperty()))

        }

        queryOptionsSwitcher.currentNodeIndexProperty().bind(Bindings.createIntegerBinding(Callable {

            val shape = queryShapeGroup.selectedToggle.userData as String

            if(shape == "circle") { 1 } else if(shape == "rect") { 0 } else -1

        }, queryShapeGroup.selectedToggleProperty()))

        randomize.textProperty().addListener { _ -> randomize() }

        width.textProperty().addListener { _ -> rebuildQuadTree()}
        height.textProperty().addListener { _ -> rebuildQuadTree()}
        capacity.textProperty().addListener { _ -> rebuildQuadTree()}

        randomize()

    }

    private fun printQueryStatistics() {

        logs.clear()
        logs.text = """ 
            Checked quad trees = ${queryStatistics.quadTrees()}
            Checked points = ${queryStatistics.points()}
            Time elapsed = ${queryStatistics.time().toLongNanoseconds()} ns = ${queryStatistics.time().toLongMilliseconds()} ms
        """.trimIndent()

    }

    @FXML private fun randomize() {

        rebuildDisposable?.dispose()

        val count = randomize.text?.toIntOrNull() ?: 100

        val qTree = quadtreeController.quadTree.value

        qTree.clear()

        repeat(count) {

            qTree.boundary.apply {

                qTree.insert(Point(Random.nextInt(x, width), Random.nextInt(y, height)))

            }

        }

        Platform.runLater { quadtreeController.quadTreeModified() }

    }

    private var rebuildDisposable: Disposable? = null
    private fun rebuildQuadTree() {

        if(rebuildDisposable != null) {

            rebuildDisposable?.dispose()
            rebuildDisposable = null

        }

        rebuildDisposable = Single.fromCallable {

            val old = quadtreeController.quadTree.value

            val new = QuadTree(Rectangle(width.text?.toIntOrNull() ?: 600, height.text?.toIntOrNull() ?: 600), capacity.text?.toIntOrNull() ?: 4)

            old.forEach { new.insert(it) }

            new

        }.delaySubscription(rebuildDelay.text?.toLongOrNull() ?: 1000, TimeUnit.MILLISECONDS)
            .observeOn(JavaFxScheduler.platform())
            .subscribeOn(Schedulers.computation())
            .subscribe({

                quadtreeController.quadTree.value = it

            }, Throwable::printStackTrace)

    }

}