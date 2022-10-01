package dmitry.molchanov.tictactoe.presentation.game.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dmitry.molchanov.tictactoe.presentation.game.model.Cell
import dmitry.molchanov.tictactoe.presentation.game.model.CellType
import dmitry.molchanov.tictactoe.presentation.game.model.ClickedStatus
import dmitry.molchanov.tictactoe.presentation.game.model.GameState
import dmitry.molchanov.tictactoe.presentation.game.model.PlayerType.CROSS
import dmitry.molchanov.tictactoe.presentation.game.model.PlayerType.ZERO
import dmitry.molchanov.tictactoe.presentation.theme.TicTacToeTheme

private val defaultGameState = GameState(
    cells = SnapshotStateList<Cell>().apply {
        add(Cell(cellType = CellType.LEFT_TOP, playerType = null))
        add(Cell(cellType = CellType.TOP, playerType = null))
        add(Cell(cellType = CellType.RIGHT_TOP, playerType = null))
        add(Cell(cellType = CellType.LEFT_CENTER, playerType = null))
        add(Cell(cellType = CellType.CENTER, playerType = null))
        add(Cell(cellType = CellType.RIGHT_CENTER, playerType = null))
        add(Cell(cellType = CellType.LEFT_BOTTOM, playerType = null))
        add(Cell(cellType = CellType.BOTTOM, playerType = null))
        add(Cell(cellType = CellType.RIGHT_BOTTOM, playerType = null))
    }
)

private const val CELL_WEIGHT = 0.3F

@Composable
/*@Preview*/
fun GameScreen(/*vm: GameViewModel = viewModel()*/) {
    TicTacToeTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        //val gameState = vm.gameState.collectAsState().value
        val gameState = remember { mutableStateOf(defaultGameState) }
        var currentPlayer = remember { CROSS }
        println("1488 currentPlayer = $currentPlayer")
        fun changePlayer() {
            currentPlayer = if (currentPlayer == CROSS) ZERO else CROSS
        }

        fun onCellClick(cell: Cell) {
            println("1488 onCellClick = $cell")
            //vm.onEvent(OnCellClick(cellType = cell.cellType, playerType = currentPlayer))
            val oldCell = gameState.value.cells.find { it == cell }
            val newCell = oldCell
                ?.copy(
                    playerType = currentPlayer, clickedStatus = when (cell.clickedStatus) {
                        ClickedStatus.CLICKABLE -> ClickedStatus.DRAW
                        ClickedStatus.DRAW -> ClickedStatus.ALREADY_CLICKED
                        ClickedStatus.ALREADY_CLICKED -> ClickedStatus.ALREADY_CLICKED
                    }
                )
                ?: error("Cell not found")
            gameState.value.cells.remove(oldCell)
            gameState.value.cells.add(newCell)
            //gameState.value = GameState(gameState.value.cells)
            changePlayer()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(gameState.value.getCell(CellType.LEFT_TOP), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.TOP), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.RIGHT_TOP), ::onCellClick)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(gameState.value.getCell(CellType.LEFT_CENTER), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.CENTER), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.RIGHT_CENTER), ::onCellClick)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(gameState.value.getCell(CellType.LEFT_BOTTOM), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.BOTTOM), ::onCellClick)
                GameCell(gameState.value.getCell(CellType.RIGHT_BOTTOM), ::onCellClick)
            }
        }
    }
}

@Composable
private fun RowScope.GameCell(cell: Cell, onClick: (Cell) -> Unit) {
    println("1488 cell = $cell")
    val color = when (cell.playerType) {
        CROSS -> Color(0xffffeb46)
        ZERO -> Color(0xff91a4fc)
        null -> Color(0xffffff)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(CELL_WEIGHT)
            .background(color)
            .clickable { onClick(cell) }
    ) {
        if(cell.clickedStatus == ClickedStatus.DRAW){
            val radius = 40f
            val animateFloat = remember { Animatable(0f) }
            LaunchedEffect(animateFloat) {
                animateFloat.animateTo(
                    targetValue = 7f,
                    animationSpec = tween(durationMillis = 3000, easing = LinearEasing))
            }
            Canvas(modifier = Modifier.fillMaxSize()){
                drawArc(
                    color = Color.Black,
                    startAngle = 0f,
                    sweepAngle = 360f * animateFloat.value,
                    useCenter = false,
                    topLeft = Offset(size.width / 4, size.height / 4),
                    size = Size(radius * 2 ,
                        radius * 2),
                    style = Stroke(10.0f)
                )
            }
        }
    }
}

private fun GameState.getCell(cellType: CellType): Cell {
    return this.cells.firstOrNull {
        it.cellType == cellType
    } ?: error("Cell not found")
}

