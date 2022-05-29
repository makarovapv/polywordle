package ui.screens

import PolyWordleGame
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enums.BoxState
import ui.themes.PolyWordleTheme

@Composable
@Preview
fun PolyWordleUI(game: PolyWordleGame) {
    var darkTheme by remember { mutableStateOf(false) }
    var gameEnded by remember { mutableStateOf(false) }
    val colors = PolyWordleTheme(darkTheme).colors
    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {

        /* title */
        Text(
            text = "PolyWordle",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            fontFamily = FontFamily(Font("font.ttf")),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            color = colors.onSurface
        )

        /* creating fields for symbols */
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.Center) {
            /* six attempts */
            items(6) { idxWord ->
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    items(5) { idxSymbol ->
                        val box = game.boxes[Pair(idxWord, idxSymbol)]!!
                        Box(
                            modifier = Modifier.size(65.dp, 85.dp).padding(2.dp)
                                .background(
                                    color =
                                    (when (box.state) {
                                        BoxState.EMPTY -> colors.onBackground
                                        BoxState.AT_THIS_POSITION -> if (darkTheme) Color(0xFF5A9216) else Color(
                                            0xFF8BC34A)
                                        BoxState.EXISTS -> if (darkTheme) Color(0xFFC79100) else Color(0xFFFFC107)
                                        BoxState.DOESNT_EXIST -> if (darkTheme) Color(0xFF373737) else Color(0xFF757575)
                                    }),
                                    shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = box.symbol,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp
                            )
                        }
                    }
                }
            }
        }

        val keyboard = mutableListOf(
            "йцукенгшщзхъ←".map { it.toString() },
            "фывапролджэ".map { it.toString() },
            "ячсмитьбю".map { it.toString() }
        )
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.Center) {
            items(3) { idx ->
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    items(keyboard[idx].size) { idxRow ->
                        val symbol = keyboard[idx][idxRow]
                        Box(
                            modifier = Modifier.size(width = 30.dp, height = 45.dp)
                                .background(
                                    color =
                                    if (game.keys[symbol] ?: (symbol == "←"))
                                        colors.onBackground
                                    else if (darkTheme)
                                        Color(0xFF373737)
                                    else
                                        Color(0xFF757575)
                                )
                                .padding(start = 4.dp, end = 4.dp)
                                .clickable {
                                    if (symbol != "←") {
                                        game.addSymbol(symbol.uppercase())
                                    } else {
                                        game.removeLastSymbol()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = symbol, fontSize = 18.sp, color = colors.onSurface)
                        }
                    }
                }
            }
        }
    }

    /* work with two buttons */
    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {

        /* theme button */
        FloatingActionButton(
            modifier = Modifier.size(64.dp, 64.dp).padding(top = 16.dp, start = 16.dp).align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colors.primary,
            onClick = {
                darkTheme = !darkTheme
                PolyWordleGame.theme = PolyWordleTheme(darkTheme)
            }) {
            if (!darkTheme)
                Icon(
                    painterResource("icons/dark_mode.svg"),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            else
                Icon(
                    painterResource("icons/light_mode.svg"),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
        }

        /* restart button */
        FloatingActionButton(
            modifier = Modifier.size(64.dp, 64.dp).padding(top = 16.dp, end = 16.dp).align(Alignment.TopEnd),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colors.primary,
            onClick = {
                gameEnded = true
            }) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
    }

    /* pop-up windows */
    Box(Modifier.fillMaxSize()) {

        /* game over */
        if (gameEnded || game.flags["failed"]!!) {
            Snackbar(Modifier.padding(8.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(12.dp)) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Игра окончена. Загаданное слово: ${game.hiddenWord}")
                    Button(
                        modifier = Modifier.wrapContentSize(align = Alignment.Center),
                        onClick = {
                            game.newGame()
                            gameEnded = false
                        },
                        colors = ButtonDefaults.textButtonColors(),
                        elevation = ButtonDefaults.elevation(0.dp)
                    ) { Text("Новая игра", color = colors.primary, fontSize = 12.sp) }
                }
            }
        }

        /* mistakes in the word */
        if (game.flags["wordNonExistent"]!!) {
            Snackbar(Modifier.padding(8.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(12.dp)) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Такого слова нет!")
                    Button(
                        modifier = Modifier.wrapContentSize(align = Alignment.Center),
                        onClick = {
                            game.flags["wordNonExistent"] = false
                        },
                        colors = ButtonDefaults.textButtonColors(),
                        elevation = ButtonDefaults.elevation(0.dp)
                    ) { Text("Скрыть", color = colors.primary, fontSize = 12.sp) }
                }
            }
        }

        /* victory! */
        Text(text = "HIDDEN WORD: ${game.hiddenWord}", modifier = Modifier.padding(8.dp).align(Alignment.BottomStart))
        if (game.flags["wordRight"]!!) {
            Snackbar(Modifier.padding(8.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(12.dp)) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Поздравляем! Вы угадали слово!")
                    Button(
                        modifier = Modifier.wrapContentSize(align = Alignment.Center),
                        onClick = {
                            game.newGame()
                            gameEnded = false
                        },
                        colors = ButtonDefaults.textButtonColors(),
                        elevation = ButtonDefaults.elevation(0.dp)
                    ) { Text("Новая игра", color = colors.primary, fontSize = 12.sp) }
                }
            }
        }
    }
}