package dmitry.molchanov.tictactoe.presentation.game

import dmitry.molchanov.tictactoe.presentation.game.model.CellType
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.BOTTOM
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.CENTER
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.LEFT_BOTTOM
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.LEFT_CENTER
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.LEFT_TOP
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.RIGHT_BOTTOM
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.RIGHT_CENTER
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.RIGHT_TOP
import dmitry.molchanov.tictactoe.presentation.game.model.CellType.TOP
import dmitry.molchanov.tictactoe.presentation.game.model.PlayerType

fun getWinnerCells(gameSnap: Map<CellType, PlayerType>): Pair<PlayerType, List<CellType>>? =
    gameSnap.getSinglePlayerWithCellsOrNull(LEFT_TOP, TOP, RIGHT_TOP)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(LEFT_CENTER, CENTER, RIGHT_CENTER)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(LEFT_BOTTOM, BOTTOM, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(TOP, CENTER, BOTTOM)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(LEFT_TOP, CENTER, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerWithCellsOrNull(RIGHT_TOP, CENTER, LEFT_BOTTOM)

private fun Map<CellType, PlayerType>.getSinglePlayerWithCellsOrNull(
    vararg cellTypes: CellType
): Pair<PlayerType, List<CellType>>? {
    var playerType: PlayerType? = null
    cellTypes.forEach { cellType ->
        val cellPlayer = this[cellType]
        when {
            cellPlayer == null -> return null
            playerType == null -> playerType = cellPlayer
            cellPlayer != playerType -> return null
        }
    }
    return playerType?.let { it to cellTypes.toList() }
}