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
import androidx.compose.ui.unit.dp
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
/*@Preview*/
fun GameScreen(/*vm: GameViewModel = viewModel()*/) {
    TicTacToeTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        //val gameState = vm.gameState.collectAsState().value
        val gameSnap = remember { SnapshotStateMap<CellType, PlayerType>() }
        var currentPlayer by remember { mutableStateOf(CROSS) }
        fun changePlayer() {
            currentPlayer = if (currentPlayer == CROSS) ZERO else CROSS
        }

        fun onCellClick(cellType: CellType) {
            gameSnap[cellType] = currentPlayer
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
                GameCell(gameSnap[LEFT_TOP]) { onCellClick(LEFT_TOP) }
                GameCell(gameSnap[TOP]) { onCellClick(TOP) }
                GameCell(gameSnap[RIGHT_TOP]) { onCellClick(RIGHT_TOP) }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(gameSnap[LEFT_CENTER]) { onCellClick(LEFT_CENTER) }
                GameCell(gameSnap[CENTER]) { onCellClick(CENTER) }
                GameCell(gameSnap[RIGHT_CENTER]) { onCellClick(RIGHT_CENTER) }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(CELL_WEIGHT)
            ) {
                GameCell(gameSnap[LEFT_BOTTOM]) { onCellClick(LEFT_BOTTOM) }
                GameCell(gameSnap[BOTTOM]) { onCellClick(BOTTOM) }
                GameCell(gameSnap[RIGHT_BOTTOM]) { onCellClick(RIGHT_BOTTOM) }
            }
        }
    }
}

@Composable
private fun RowScope.GameCell(playerType: PlayerType?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .weight(CELL_WEIGHT)
            .clickable { onClick() }
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
        animateFloat.animateTo(
            targetValue = 7f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
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
        animVal.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
        animVal2.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
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

