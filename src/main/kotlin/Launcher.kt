import io.reactivex.plugins.RxJavaPlugins
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import utils.loadFXML

@JvmName("Launcher")
fun main(args: Array<String>) = Application.launch(*args) //for compatibility

class Launcher : Application() {

    override fun start(primaryStage: Stage) {

        primaryStage.apply {

            scene = Scene(loadFXML("layouts/home.fxml"))
            title = "QuadTree"
            icons.add(Image("images/launcher.png"))
            show()

        }

        RxJavaPlugins.setErrorHandler { println("Redunant error") }

    }

}