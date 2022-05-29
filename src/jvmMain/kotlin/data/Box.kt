package data

import enums.BoxState

/* a box for each symbol */
data class Box(
    var state: BoxState = BoxState.EMPTY,
    var symbol: String = "",
)