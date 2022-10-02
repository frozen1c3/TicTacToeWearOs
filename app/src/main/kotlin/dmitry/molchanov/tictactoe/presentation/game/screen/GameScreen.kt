package dmitry.molchanov.tictactoe.presentation.game.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import dmitry.molchanov.tictactoe.presentation.game.getWinnerCells
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
import dmitry.molchanov.tictactoe.presentation.game.model.PlayerType.CROSS
import dmitry.molchanov.tictactoe.presentation.game.model.PlayerType.ZERO
import dmitry.molchanov.tictactoe.presentation.theme.TicTacToeTheme

private const val CELL_WEIGHT = 0.3F

@Composable
fun GameScreen() {
    TicTacToeTheme {
        //TODO optimize recompose
        val gameSnap = remember { SnapshotStateMap<CellType, PlayerType>() }
        val positioningState = remember { mutableStateOf(HashMap<CellType, Offset>()) }
        var currentPlayer by remember { mutableStateOf(CROSS) }
        var winnerWithCells by remember { mutableStateOf<Pair<PlayerType, List<CellType>>?>(null) }

        fun onCellClick(cellType: CellType) {
            if (winnerWithCells != null || gameSnap.size == 9) {
                winnerWithCells = null
                gameSnap.clear()
                return
            }
            if (gameSnap[cellType] == null) {
                gameSnap[cellType] = currentPlayer
            }
            getWinnerCells(gameSnap)?.let { winner ->
                winnerWithCells = winner
            }
            currentPlayer = if (currentPlayer == CROSS) ZERO else CROSS
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
                GameCell(LEFT_TOP, gameSnap[LEFT_TOP], positioningState, ::onCellClick)
                GameCell(TOP, gameSnap[TOP], positioningState, ::onCellClick)
                GameCell(RIGHT_TOP, gameSnap[RIGHT_TOP], positioningState, ::onCellClick)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(LEFT_CENTER, gameSnap[LEFT_CENTER], positioningState, ::onCellClick)
                GameCell(CENTER, gameSnap[CENTER], positioningState, ::onCellClick)
                GameCell(RIGHT_CENTER, gameSnap[RIGHT_CENTER], positioningState, ::onCellClick)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(LEFT_BOTTOM, gameSnap[LEFT_BOTTOM], positioningState, ::onCellClick)
                GameCell(BOTTOM, gameSnap[BOTTOM], positioningState, ::onCellClick)
                GameCell(RIGHT_BOTTOM, gameSnap[RIGHT_BOTTOM], positioningState, ::onCellClick)
            }
        }
        winnerWithCells?.let {
            val firstCell = it.second.first()
            val lastCell = it.second.last()
            DrawGameOverLine(
                startOffset = positioningState.value[firstCell] ?: error("Start offset is null"),
                endOffset = positioningState.value[lastCell] ?: error("End offset is null")
            )
        }
    }
}

@Composable
private fun DrawGameOverLine(startOffset: Offset, endOffset: Offset) {
    val animVal = remember { Animatable(0f) }
    val xDifference = remember {
        if (startOffset.x < endOffset.x) endOffset.x - startOffset.x
        else (startOffset.x - endOffset.x) * -1
    }
    val yDifference = remember {
        if (startOffset.y < endOffset.y) endOffset.y - startOffset.y
        else (startOffset.y - endOffset.y) * -1
    }
    LaunchedEffect(animVal) {
        animVal.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            color = Color.Blue,
            start = startOffset,
            end = Offset(
                x = startOffset.x + animVal.value * xDifference,
                y = startOffset.y + yDifference * animVal.value
            ),
            strokeWidth = 20f
        )
    }
}

@Composable
private fun RowScope.GameCell(
    cellType: CellType,
    playerType: PlayerType?,
    positioningState: State<HashMap<CellType, Offset>>,
    onClick: (CellType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(CELL_WEIGHT)
            .clickable { onClick(cellType) }
            .onGloballyPositioned {
                val size = it.size
                val position = it.positionInRoot()
                positioningState.value[cellType] = Offset(
                    x = position.x + size.width / 2,
                    y = position.y + size.height / 2
                )
            }
    ) {
        when (playerType) {
            CROSS -> DrawCross()
            ZERO -> DrawCircle()
            null -> Unit
        }
    }
}

@Composable
private fun DrawCircle() {
    val radius = 45f
    val animateFloat = remember { Animatable(0f) }
    LaunchedEffect(animateFloat) {
        animateFloat.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawArc(
            color = Color.Red,
            startAngle = 0f,
            sweepAngle = 360f * animateFloat.value,
            useCenter = false,
            topLeft = Offset(size.width / 6, size.height / 6),
            size = Size(radius * 2, radius * 2),
            style = Stroke(10.0f)
        )
    }
}

@Composable
private fun DrawCross() {
    val animVal = remember { Animatable(0f) }
    val animVal2 = remember { Animatable(0f) }
    LaunchedEffect(animVal) {
        animVal.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
        animVal2.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        drawLine(
            color = Color.Green,
            start = Offset(0f, 0f),
            end = Offset(animVal.value * size.width, animVal.value * size.height),
            strokeWidth = 10f
        )
        drawLine(
            color = Color.Green,
            start = Offset(size.width, 0f),
            end = Offset(size.width - (animVal2.value * size.width), animVal2.value * size.height),
            strokeWidth = 10f
        )
    }
}

