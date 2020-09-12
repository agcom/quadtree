package nodes

import com.jfoenix.controls.JFXTextField
import javafx.scene.control.TextFormatter
import java.util.function.UnaryOperator

class IntField : JFXTextField {

    constructor() : this(null)

    constructor(text: String?) : super(text) {

        textFormatter = TextFormatter<Any>(integerFilter)

    }

    companion object {

        private val integerFilter = UnaryOperator { change: TextFormatter.Change? ->
            val input = change!!.text
            if (input.matches(Regex("[0-9]*"))) change
            else null
        }

    }
}