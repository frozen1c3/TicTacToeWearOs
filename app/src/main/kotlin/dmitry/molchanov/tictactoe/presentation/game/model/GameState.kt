package dmitry.molchanov.tictactoe.presentation.game.model

import androidx.compose.runtime.snapshots.SnapshotStateList

data class GameState(
    val cells: SnapshotStateList<Cell>
)

