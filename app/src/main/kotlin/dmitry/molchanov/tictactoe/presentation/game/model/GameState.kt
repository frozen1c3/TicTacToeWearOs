package dmitry.molchanov.tictactoe.presentation.game.model

import androidx.compose.runtime.snapshots.SnapshotStateMap

data class GameState(
    val cells: SnapshotStateMap<CellType, PlayerType>
)

