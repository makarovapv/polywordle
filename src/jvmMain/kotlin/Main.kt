// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File

// working with a hidden word from the dictionary
fun hiddenWord(): Pair<String, List<String>> {
    val hiddenWord = File("singular.txt").readLines().random()
    val symbols = hiddenWord.split("").toMutableList()

    // if there is a problem with parsing and spaces
    if (symbols[0] == "") {
        symbols.removeFirst()
    }
    if (symbols.last() == "") {
        symbols.removeLast()
    }

    return Pair(hiddenWord, symbols)
}


@Composable
@Preview
fun PolyWordle() {

//    Text(text = "${hiddenWord()}")
    Text(text = "POLYWORDLE",
        textAlign = TextAlign.Center,
        style = typography.h4,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Green)
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(45.dp), verticalArrangement = Arrangement.Center) {
        items(6) {
            LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                items(5) {
                    Box(
                        modifier = Modifier.size(65.dp, 85.dp).clip(RoundedCornerShape(50.dp)).padding(2.dp)
                            .background(color = Color.LightGray)
                    )
                }
            }
        }
    }
//    var text by remember { mutableStateOf("Hello, World!") }
//
//    MaterialTheme {
//        Button(onClick = {
//            text = "Hello, Desktop!"
//        }) {
//            Text(text)
//        }
//    }
}

fun main() = application {
    Window(title = "PolyWordle",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(position = WindowPosition(540.dp,110.dp), size = DpSize(450.dp, 640.dp))) {
        PolyWordle()
    }
}
