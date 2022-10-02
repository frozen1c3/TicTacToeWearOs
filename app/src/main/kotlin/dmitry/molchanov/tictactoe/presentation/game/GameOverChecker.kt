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

fun getWinner(gameSnap: Map<CellType, PlayerType>): PlayerType? =
    gameSnap.getSinglePlayerOrNull(LEFT_TOP, TOP, RIGHT_TOP)
        ?: gameSnap.getSinglePlayerOrNull(LEFT_CENTER, CENTER, RIGHT_CENTER)
        ?: gameSnap.getSinglePlayerOrNull(LEFT_BOTTOM, BOTTOM, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerOrNull(LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM)
        ?: gameSnap.getSinglePlayerOrNull(TOP, CENTER, BOTTOM)
        ?: gameSnap.getSinglePlayerOrNull(RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerOrNull(LEFT_TOP, CENTER, RIGHT_BOTTOM)
        ?: gameSnap.getSinglePlayerOrNull(RIGHT_TOP, CENTER, LEFT_BOTTOM)

private fun Map<CellType, PlayerType>.getSinglePlayerOrNull(
    vararg cellTypes: CellType
): PlayerType? {
    var playerType: PlayerType? = null
    cellTypes.forEach { cellType ->
        val cellPlayer = this[cellType]
        when {
            cellPlayer == null -> return null
            playerType == null -> playerType = cellPlayer
            cellPlayer != playerType -> return null
        }
    }
    return playerType
}