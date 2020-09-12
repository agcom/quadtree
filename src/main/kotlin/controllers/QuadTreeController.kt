package controllers

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import javafx.beans.property.*
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import nodes.*
import quadtree.QuadTree
import quadtree.QueryStatistics
import quadtree.geometry.Point
import quadtree.geometry.Rectangle
import utils.executor
import utils.quadTreeModif
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.ConcurrentModificationException
import kotlin.time.ExperimentalTime

@ExperimentalTime
class QuadTreeController: Initializable {

    val quadTree = SimpleObjectProperty<QuadTree>()
    val query = SimpleObjectProperty<AreaShape>()
    val erase = SimpleBooleanProperty(false)
    val drawDelay = SimpleLongProperty(5)
    val queryDelay = SimpleLongProperty(10)
    private val mouseX = SimpleDoubleProperty()
    private val mouseY = SimpleDoubleProperty()
    private val width = SimpleIntegerProperty()
    private val height = SimpleIntegerProperty()

    @FXML private lateinit var queryCanvas: Canvas
    @FXML private lateinit var quadTreeCanvas: Canvas
    @FXML private lateinit var queryArea: BorderPane
    @FXML private lateinit var sizePane: StackPane

    constructor(quadTree: QuadTree) {

        this.quadTree.value = quadTree
        width.value = quadTree.boundary.width
        height.value = quadTree.boundary.height

    }

    constructor() : this(QuadTree(Rectangle(0, 0, 600, 600)))

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        mouseX.value = (width.value / 3).toDouble()
        mouseY.value = (height.value / 3).toDouble()

        sizePane.also {

            it.prefWidthProperty().bind(width)
            it.prefHeightProperty().bind(height)

            it.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE)
            it.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE)

        }

        queryCanvas.also {

            it.widthProperty().bind(width)
            it.heightProperty().bind(height)

        }

        quadTreeCanvas.also {

            it.widthProperty().bind(width)
            it.heightProperty().bind(height)

        }

        queryArea.also {

            it.layoutXProperty().bind(mouseX.subtract(it.widthProperty().divide(2)))
            it.layoutYProperty().bind(mouseY.subtract(it.heightProperty().divide(2)))

            query.addListener { _, _, query ->

                it.center = query.node
                it.autosize()
                query.move(it.layoutX.toInt(), it.layoutY.toInt())

                query()

            }

            it.layoutXProperty().addListener { _, _, x -> query.value?.move(x.toInt(), it.layoutY.toInt()); query() }
            it.layoutYProperty().addListener { _, _, y -> query.value?.move(it.layoutX.toInt(), y.toInt()); query() }

        }

        quadTree.also {

            it.addListener { _, _, qTree ->

                width.value = qTree.boundary.width
                height.value = qTree.boundary.height
                quadTreeModified()

            }

            quadTree.value.draw(quadTreeCanvas)

        }

        executor.schedule({

            sizePane.scene.setOnKeyPressed { erase.value = it.isShiftDown }
            sizePane.scene.setOnKeyReleased { erase.value = it.isShiftDown }

        }, 1, TimeUnit.SECONDS)

    }

    private var queryDisposable: Disposable? = null
    var queryStatistics = SimpleObjectProperty<QueryStatistics>(null)
    private fun query() {

        if(queryDisposable != null) {

            queryDisposable?.dispose()
            queryDisposable = null
            queryStatistics.value?.reset()
            queryCanvas.clear()

        }

        if(query.value == null) return

        queryDisposable = quadTree.value.query(query.value!!.area, queryStatistics.value)
            .delaySubscription(queryDelay.value ?: 10, TimeUnit.MILLISECONDS)
            .subscribe({ it.draw(queryCanvas, paint = Color.RED) }, {

                if(it is ConcurrentModificationException) {

                    println("Expected exception")
                    query()

                } else it.printStackTrace()

            })

    }

    private fun erase() {

        quadTree.value.query(query.value!!.area, queryStatistics.value)
            .subscribeOn(Schedulers.from(quadTreeModif))
            .map { quadTree.value.delete(it) }
            .doOnComplete { quadTreeModified() }
            .subscribe({}, Throwable::printStackTrace)

    }

    private var drawDisposable: Disposable? = null
    fun quadTreeModified() {

        if(drawDisposable != null) {

            drawDisposable?.dispose()
            drawDisposable = null

        }

        drawDisposable = Completable.fromAction {

            quadTreeCanvas.clear()
            quadTree.value?.draw(quadTreeCanvas)
            query()

        }.subscribeOn(JavaFxScheduler.platform())
            .delaySubscription(drawDelay.value ?: 5, TimeUnit.MILLISECONDS).subscribe()

    }

    @FXML private fun onMouseClicked(event: MouseEvent) {

        if(erase.value) {

            erase()

        } else quadTreeModif.submit {

            quadTree.value.insert(Point(event.x.toInt(), event.y.toInt()))

            Platform.runLater { quadTreeModified() }

        }

    }

    @FXML private fun onMouseMoved(event: MouseEvent) {

        mouseX.value = event.x
        mouseY.value = event.y

    }

    @FXML private fun onMouseDragged(event: MouseEvent) {

        onMouseMoved(event)

        if(erase.value) erase()
        else onMouseClicked(event)

    }

}