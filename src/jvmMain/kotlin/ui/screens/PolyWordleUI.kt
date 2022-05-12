// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import java.io.File

@Composable
@Preview
fun PolyWordleUI(game: PolyWordleGame) {
    var darkTheme by remember { mutableStateOf(false) }
    var gameEnded by remember { mutableStateOf(false) }
    val colors = PolyWordleTheme(darkTheme).colors
    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Text(
            text = "PolyWordle",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            fontFamily = FontFamily(Font(File("font.ttf"))),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            color = colors.onSurface
        )
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.Center) {
            items(6) { idxWord ->
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    items(5) { idxSymbol ->
                        val box = game.boxes[Pair(idxWord, idxSymbol)]!!
                        Box(
                            modifier = Modifier.size(65.dp, 85.dp).padding(2.dp)
                                .background(color = box.getColor(), shape = RoundedCornerShape(12.dp)),
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
    }
    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        FloatingActionButton(
            modifier = Modifier.size(64.dp, 64.dp).padding(top = 16.dp, start = 16.dp).align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = colors.primary,
            onClick = {
                //darkTheme = !darkTheme
                if (game.addSymbol(('А'..'Я').random().toString())) {
                    gameEnded = true
                }
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
    Box(Modifier.fillMaxSize()) {
        if (gameEnded) {
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
                    ) { Text("Новая игра", color = colors.primary, fontSize = 12.sp)}
                }
            }
        }
        Text(text = "HIDDEN WORD: ${game.hiddenWord}", modifier = Modifier.padding(8.dp).align(Alignment.BottomStart))
        if (game.flags["wordRight"]!!) {
            Snackbar(Modifier.padding(8.dp).align(Alignment.BottomCenter), shape = RoundedCornerShape(12.dp)) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Поздравляем!. Загаданное слово: ${game.hiddenWord}")
                    Button(
                        modifier = Modifier.wrapContentSize(align = Alignment.Center),
                        onClick = {
                            game.newGame()
                            gameEnded = false
                        },
                        colors = ButtonDefaults.textButtonColors(),
                        elevation = ButtonDefaults.elevation(0.dp)
                    ) { Text("Новая игра", color = colors.primary, fontSize = 12.sp)}
                }
            }
        }
    }
}


