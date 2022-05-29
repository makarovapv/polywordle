import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.Box
import enums.BoxState
import ui.screens.PolyWordleUI
import ui.themes.PolyWordleTheme

class PolyWordleGame(
    var boxes: MutableMap<Pair<Int, Int>, Box>,
    var flags: MutableMap<String, Boolean>,
    var keys: MutableMap<String, Boolean>,
) {
    companion object {
        var theme = PolyWordleTheme(false)
        fun clearWords(boxes: MutableMap<Pair<Int, Int>, Box>) {
            boxes.apply {
                for (word in 0..5) {
                    for (symbol in 0..4) {
                        put(Pair(word, symbol), Box())
                    }
                }
            }
        }

        /* clear keyboard */
        fun clearKeys(keys: MutableMap<String, Boolean>) {
            "йцукенгшщзхъфывапролджэячсмитьбю".forEach {
                keys[it.toString()] = true
            }
        }

        @JvmStatic
        fun main(args: Array<String>) = application {
            val boxes = remember { mutableStateMapOf<Pair<Int, Int>, Box>().apply { clearWords(this) } }
            val keys = remember { mutableStateMapOf<String, Boolean>().apply { clearKeys(this) } }
            val flags = remember {
                mutableStateMapOf<String, Boolean>().apply {
                    put("wordRight", false)
                    put("failed", false)
                    put("wordNonExistent", false)
                }
            }

            val game = PolyWordleGame(boxes, flags, keys)

            /* getting started with the app */
            Window(
                title = "PolyWordle",
                onCloseRequest = ::exitApplication,
                state = rememberWindowState(position = WindowPosition(540.dp, 0.dp), size = DpSize(450.dp, 800.dp)),
                resizable = false,
                onKeyEvent = {
                    /* backspace key */
                    if ((it.type == KeyEventType.KeyDown) && ((it.nativeKeyEvent as java.awt.event.KeyEvent).keyCode == 8)) {
                        game.removeLastSymbol()
                    }
                    /* enter key */
                    if ((it.type == KeyEventType.KeyDown) && ((it.nativeKeyEvent as java.awt.event.KeyEvent).keyCode == 10)) {
                        game.submitWord()
                    }
                    if (it.isTypedEvent) {
                        val keyEvent = it.nativeKeyEvent as java.awt.event.KeyEvent
                        val char = keyEvent.keyChar.uppercase()[0]
                        if (char in 'А'..'Я') {
                            game.addSymbol(char.toString())
                        }
                    }
                    true
                }
            ) {
                PolyWordleUI(game)
            }
        }
    }

    private val lines = useResource("singular.txt") {
        it.reader().readLines()
    }

    /* choosing a new word */
    private fun newHiddenWord() {
        hiddenWord = lines.random().uppercase()
        hiddenWordSymbols = hiddenWord.split("").filter { it != "" }
        hiddenWordSymbolsStat = mutableMapOf()
        resetStat()
    }

    /* getting hidden word statistics */
    private fun resetStat() {
        hiddenWordSymbolsStat = mutableMapOf()
        hiddenWordSymbols.forEach {
            if (it !in hiddenWordSymbolsStat.keys) {
                hiddenWordSymbolsStat[it] = 1
            } else {
                hiddenWordSymbolsStat[it] = hiddenWordSymbolsStat[it]!! + 1
            }
        }
    }

    lateinit var hiddenWord: String
    private lateinit var hiddenWordSymbols: List<String>
    private lateinit var hiddenWordSymbolsStat: MutableMap<String, Int>
    private var x = 0 // word
    private var y = 0 // symbol
    private var wordOngoing = true

    /* adding a new symbol from a word */
    fun addSymbol(symbol: String): Boolean {
        return if (wordOngoing) {
            boxes[Pair(x, y)] = Box(
                symbol = symbol
            )
            y++
            if (y == 5) {
                wordOngoing = false
            }
            true
        } else {
            false
        }
    }

    /* delete a last symbol from a word */
    fun removeLastSymbol() {
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

    /* check the final word */
    fun submitWord() {
        if (!wordOngoing) {
            var word = ""
            for (i in 0..4) {
                word += boxes[Pair(x, i)]!!.symbol
            }
            if (word.lowercase() in lines) {
                for (y in 0..4) {
                    val box = boxes[Pair(x, y)]!!
                    boxes[Pair(x, y)] = Box(
                        when (box.symbol) {
                            in hiddenWordSymbols -> {
                                /* first we check the exact position of the symbol */
                                if (y in getSymbolPos(box.symbol) && hiddenWordSymbolsStat[box.symbol]!! >= 1) {
                                    hiddenWordSymbolsStat[box.symbol] = hiddenWordSymbolsStat[box.symbol]!! - 1
                                    BoxState.AT_THIS_POSITION
                                } else {
                                    BoxState.DOESNT_EXIST
                                }
                            }
                            else -> BoxState.DOESNT_EXIST
                        }, box.symbol
                    )
                }
                for (y in 0..4) {
                    val box = boxes[Pair(x, y)]!!
                    boxes[Pair(x, y)] = Box(
                        when (box.symbol) {
                            in hiddenWordSymbols -> {
                                /* if there is no exact position, then we look at the simple presence of
                                 a symbol in the word */
                                if (y in getSymbolPos(box.symbol)) {
                                    BoxState.AT_THIS_POSITION
                                } else {
                                    if (hiddenWordSymbolsStat[box.symbol]!! >= 1) {
                                        hiddenWordSymbolsStat[box.symbol] = hiddenWordSymbolsStat[box.symbol]!! - 1
                                        BoxState.EXISTS
                                    } else {
                                        BoxState.DOESNT_EXIST
                                    }
                                }
                            }
                            else -> {
                                keys[box.symbol.lowercase()] = false
                                BoxState.DOESNT_EXIST
                            }
                        }, box.symbol
                    )
                }
                resetStat()
                flags["wordRight"] = word == hiddenWord
                wordOngoing = true
                x++
                y = 0
                if (x == 6 && !flags["wordRight"]!!) {
                    flags["failed"] = true
                }
            } else {
                flags["wordNonExistent"] = true
            }
        }
    }

    /* getting the position of each symbol */
    private fun getSymbolPos(symbol: String): List<Int> {
        val result = mutableListOf<Int>()
        hiddenWordSymbols.forEachIndexed { idx, sym ->
            if (symbol == sym) {
                result.add(idx)
            }
        }
        return result
    }

    /* reset old data and start a new game */
    fun newGame() {
        x = 0
        y = 0
        flags["wordRight"] = false
        flags["failed"] = false
        clearWords(boxes)
        clearKeys(keys)
        newHiddenWord() // needed to generate a new word
    }

    init {
        newHiddenWord() // it is necessary for correct operation
    }
}