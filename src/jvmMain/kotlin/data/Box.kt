package data

import androidx.compose.ui.graphics.Color
import enums.BoxState

data class Box(
    var state: BoxState = BoxState.EMPTY,
    var symbol: String = ""
) {
    fun getColor(): Color =
        when (state) {
            BoxState.EMPTY -> Color.LightGray
            BoxState.EXISTS -> Color.Yellow
            BoxState.AT_THIS_POSITION -> Color.Green
            BoxState.DOESNT_EXIST -> Color.Gray
        }
}