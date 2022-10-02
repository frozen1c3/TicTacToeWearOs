package dmitry.molchanov.tictactoe

import dmitry.molchanov.tictactoe.presentation.game.getWinner
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WinnerTest {

    @Test
    fun `check top row success`() {
        checkLine(LEFT_TOP, TOP, RIGHT_TOP)
    }

    @Test
    fun `check middle row success`() {
        checkLine(LEFT_CENTER, CENTER, RIGHT_CENTER)
    }

    @Test
    fun `check bottom row success`() {
        checkLine(LEFT_BOTTOM, BOTTOM, RIGHT_BOTTOM)
    }

    @Test
    fun `check left column success`() {
        checkLine(LEFT_BOTTOM, BOTTOM, RIGHT_BOTTOM)
    }

    @Test
    fun `check center column success`() {
        checkLine(TOP, CENTER, BOTTOM)
    }

    @Test
    fun `check right column success`() {
        checkLine(RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM)
    }

    @Test
    fun `check first diagonal success`() {
        checkLine(RIGHT_TOP, CENTER, LEFT_BOTTOM)
    }

    @Test
    fun `check second diagonal success`() {
        checkLine(LEFT_TOP, CENTER, RIGHT_BOTTOM)
    }

    @Test
    fun `check unsuccess variants`(){
        checkNoWinner(LEFT_TOP, CENTER, RIGHT_TOP)
        checkNoWinner(LEFT_TOP, TOP, BOTTOM)
        checkNoWinner(BOTTOM, CENTER, RIGHT_TOP)
    }

    private fun checkNoWinner(vararg cellTypes: CellType) {
        listOf(PlayerType.ZERO, PlayerType.CROSS).forEach { player ->
            val gameSnap = getSnap(player, *cellTypes)
            val winner = getWinner(gameSnap)
            assertNull(winner)
        }
    }
    private fun checkLine(vararg cellTypes: CellType) {
        listOf(PlayerType.ZERO, PlayerType.CROSS).forEach { player ->
            val gameSnap = getSnap(player, *cellTypes)
            val winner = getWinner(gameSnap)
            assertEquals(player, winner)
        }
    }

    private fun getSnap(
        playerType: PlayerType,
        vararg cellTypes: CellType
    ): Map<CellType, PlayerType> {
        val map = HashMap<CellType, PlayerType>()
        cellTypes.forEach { cellType ->
            map[cellType] = playerType
        }
        return map
    }
}