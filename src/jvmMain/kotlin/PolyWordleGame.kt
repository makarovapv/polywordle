import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.Box
import enums.BoxState
import java.io.File

class PolyWordleGame(var boxes: MutableMap<Pair<Int, Int>, Box>, var flags: MutableMap<String, Boolean>) {
    companion object {
        private fun clearWords(boxes: MutableMap<Pair<Int, Int>, Box>) {
            boxes.apply {
                for (word in 0..5) {
                    for (symbol in 0..4) {
                        put(Pair(word, symbol), Box())
                    }
                }
            }
        }

        @JvmStatic
        fun main(args: Array<String>) = application {
            val boxes = remember {
                mutableStateMapOf<Pair<Int, Int>, Box>().apply {
                    clearWords(this)
                }
            }
            val flags = remember {
                mutableStateMapOf<String, Boolean>().apply {
                    put("wordRight", false)
                }
            }
            val game = PolyWordleGame(boxes, flags)
            Window(
                title = "PolyWordle",
                onCloseRequest = ::exitApplication,
                state = rememberWindowState(position = WindowPosition(540.dp, 110.dp), size = DpSize(450.dp, 640.dp)),
                resizable = false,
                onKeyEvent = {
                    if ((it.type == KeyEventType.KeyDown) && ((it.nativeKeyEvent as java.awt.event.KeyEvent).keyCode == 8)) {
                        game.removeLastSymbol()
                    }
                    if ((it.type == KeyEventType.KeyDown) && ((it.nativeKeyEvent as java.awt.event.KeyEvent).keyCode == 10)) {
                        game.submitWord()
                    }
                    if (it.isTypedEvent) {
                        val keyEvent = it.nativeKeyEvent as java.awt.event.KeyEvent
                        game.addSymbol(keyEvent.keyChar.uppercase())
                    }
                    true
                }
            ) {
                PolyWordleUI(game)
            }
        }
    }

    private fun newHiddenWord() {
        hiddenWord = File("singular.txt").readLines().random().uppercase()
        hiddenWordSymbols = hiddenWord.split("").filter { it != "" }
    }

    lateinit var hiddenWord: String
    private lateinit var hiddenWordSymbols: List<String>
    var x = 0
    var y = 0
    var wordOngoing = true

    fun addSymbol(symbol: String): Boolean {
        if (wordOngoing) {
            boxes[Pair(x, y)] = Box(
                symbol = symbol
            )
            y++
            if (y == 5) {
                wordOngoing = false
            }
            return x == 6
        } else {
            return false
        }
    }

    private fun removeLastSymbol() {
        if (y != 0) {
            y--
        } else {
            return
        }
        boxes[Pair(x, y)] = Box()
        if (!wordOngoing) {
            wordOngoing = true
        }
    }

    fun submitWord() {
        if (!wordOngoing) {
            boxes.onEach { (idx, box) ->
                if (idx.first == x) {
                    boxes[idx] = Box(
                        when (box.symbol) {
                            in hiddenWordSymbols -> {
                                if (idx.second in getSymbolPos(box.symbol)) {
                                    BoxState.AT_THIS_POSITION
                                } else {
                                    BoxState.EXISTS
                                }
                            }
                            else -> BoxState.DOESNT_EXIST
                        }, box.symbol
                    )
                }
            }
            if (boxes.filter { it.key.first == x }.values.all { it.state == BoxState.AT_THIS_POSITION }) {
                flags.put("wordRight", true)
            }
            wordOngoing = true
            x++
            y = 0
        }
    }

    private fun getSymbolPos(symbol: String): List<Int> {
        val result = mutableListOf<Int>()
        hiddenWordSymbols.forEachIndexed { idx, sym ->
            if (symbol == sym) {
                result.add(idx)
            }
        }
        return result
    }

    fun newGame() {
        x = 0
        y = 0
        flags["wordRight"] = false
        clearWords(boxes)
        newHiddenWord()
    }

    init {
        newHiddenWord()
        println(hiddenWord)
        println(hiddenWordSymbols)
    }
}