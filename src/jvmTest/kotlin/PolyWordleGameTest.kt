import data.Box
import enums.BoxState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PolyWordleGameTest {
    private val game: PolyWordleGame

    init {
        val boxes = mutableMapOf<Pair<Int, Int>, Box>().apply { PolyWordleGame.clearWords(this) }
        val keys = mutableMapOf<String, Boolean>().apply { PolyWordleGame.clearKeys(this) }
        val flags = mutableMapOf<String, Boolean>().apply {
            put("wordRight", false)
            put("failed", false)
            put("wordNonExistent", false)
        }
        game = PolyWordleGame(boxes, flags, keys)
    }

    @Test
    fun rightWordTest() {
        game.newGame()
        game.hiddenWord.uppercase().forEach {
            assertTrue(game.addSymbol(it.toString()))
        }
        game.submitWord()
        for (y in 0..4) {
            val box = game.boxes[Pair(0, y)]!!
            assertEquals(game.hiddenWord[y].toString(), box.symbol)
            assertEquals(BoxState.AT_THIS_POSITION, box.state)
        }
        assertTrue(game.flags["wordRight"]!!)
    }

    @Test
    fun wrongWordTest() {
        game.newGame()
        var word = "купол"
        if (word == game.hiddenWord) {
            word = "плита"
        }
        repeat(5) {
            word.uppercase().forEach {
                assertTrue(game.addSymbol(it.toString()))
            }
            game.submitWord()
            for (y in 0..4) {
                val box = game.boxes[Pair(0, y)]!!
                assertEquals(word[y].uppercase(), box.symbol)
            }
            assertFalse(game.flags["wordRight"]!!)
            assertFalse(game.flags["wordNonExistent"]!!)
        }
        word.uppercase().forEach {
            assertTrue(game.addSymbol(it.toString()))
        }
        game.submitWord()
        for (y in 0..4) {
            val box = game.boxes[Pair(0, y)]!!
            assertEquals(word[y].uppercase(), box.symbol)
        }
        assertFalse(game.flags["wordRight"]!!)
        assertFalse(game.flags["wordNonExistent"]!!)
        assertTrue(game.flags["failed"]!!)
    }

    @Test
    fun nonExistentWordTest() {
        game.newGame()
        "мяумя".uppercase().forEach {
            assertTrue(game.addSymbol(it.toString()))
        }
        game.submitWord()
        assertTrue(game.flags["wordNonExistent"]!!)
        // Emulate click on "Скрыть"
        game.flags["wordNonExistent"] = false
        // Clear word
        repeat(5) { game.removeLastSymbol() }
    }
}