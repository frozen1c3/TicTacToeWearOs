package dmitry.molchanov.tictactoe.presentation.game.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import dmitry.molchanov.tictactoe.R
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
import java.lang.Float.min

private const val CELL_SIZE = 9

@Composable
fun GameScreen() {
    TicTacToeTheme {
        //TODO optimize recompose
        val gameSnap = remember { SnapshotStateMap<CellType, PlayerType>() }
        val positioningState = remember { mutableStateOf(HashMap<CellType, Offset>()) }
        var currentPlayer by remember { mutableStateOf(CROSS) }
        var winnerWithCells by remember { mutableStateOf<Pair<PlayerType, List<CellType>>?>(null) }

        fun onCellClick(cellType: CellType) {
            if (winnerWithCells != null || gameSnap.size == CELL_SIZE) {
                winnerWithCells = null
                gameSnap.clear()
                return
            }
            if (gameSnap[cellType] == null) {
                gameSnap[cellType] = currentPlayer
                currentPlayer = if (currentPlayer == CROSS) ZERO else CROSS
            }
            getWinnerCells(gameSnap)?.let { winner ->
                winnerWithCells = winner
            }
        }

        val borderAnim = remember { Animatable(0f) }
        LaunchedEffect(borderAnim) {
            borderAnim.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
        }
        val borderColor = MaterialTheme.colors.primary
        val filedWidth = dimensionResource(id = R.dimen.field_width).value
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(modifier = Modifier
                //.fillMaxSize()
                //.background()
                .align(Alignment.Center)
                .padding(16.dp)
                .drawBehind {
                    drawFiled(
                        size = size,
                        color = borderColor,
                        borderAnim = borderAnim,
                        strokeWidth = filedWidth
                    )
                }, columns = GridCells.Fixed(count = 3), content = {
                items(
                    arrayOf(
                        LEFT_TOP,
                        TOP,
                        RIGHT_TOP,
                        LEFT_CENTER,
                        CENTER,
                        RIGHT_CENTER,
                        LEFT_BOTTOM,
                        BOTTOM,
                        RIGHT_BOTTOM,
                    )
                ) { cellType ->
                    GameCell(cellType, gameSnap[cellType], positioningState, ::onCellClick)
                }
            })
        }
        winnerWithCells?.let {
            val winner = it.first
            val firstCell = it.second.firstOrNull() ?: return@let
            val lastCell = it.second.lastOrNull() ?: return@let
            DrawGameOverLine(
                startOffset = positioningState.value[firstCell] ?: error("Start offset is null"),
                endOffset = positioningState.value[lastCell] ?: error("End offset is null"),
                color = if (winner == ZERO) MaterialTheme.colors.secondary else MaterialTheme.colors.secondaryVariant,
            )
        }
    }
}

private fun DrawScope.drawFiled(
    size: Size,
    color: Color,
    borderAnim: Animatable<Float, AnimationVector1D>,
    strokeWidth: Float
) {
    drawLine(
        color,
        Offset(0f, size.height / 3),
        Offset(size.width * borderAnim.value, size.height / 3),
        strokeWidth
    )
    drawLine(
        color,
        Offset(0f, size.height / 1.5f),
        Offset(size.width * borderAnim.value, size.height / 1.5f),
        strokeWidth
    )
    drawLine(
        color,
        Offset(size.width / 3, 0f),
        Offset(size.width / 3, size.height * borderAnim.value),
        strokeWidth
    )
    drawLine(
        color,
        Offset(size.width / 1.5f, 0f),
        Offset(size.width / 1.5f, size.height * borderAnim.value),
        strokeWidth
    )
}

@Composable
private fun DrawGameOverLine(startOffset: Offset, endOffset: Offset, color: Color) {
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
    val figureWidth = dimensionResource(id = R.dimen.game_over_line).value
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            color = color,
            start = startOffset,
            end = Offset(
                x = startOffset.x + animVal.value * xDifference,
                y = startOffset.y + yDifference * animVal.value
            ),
            strokeWidth = figureWidth
        )
    }
}

@Composable
fun GameCell(
    cellType: CellType,
    playerType: PlayerType?,
    positioningState: State<HashMap<CellType, Offset>>,
    onClick: (CellType) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onClick(cellType)
                })
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
    val color = MaterialTheme.colors.secondaryVariant
    val animateFloat = remember { Animatable(0f) }
    val circleMargin = dimensionResource(id = R.dimen.circle_margin).value
    val figureWidth = dimensionResource(id = R.dimen.figure_width).value
    LaunchedEffect(animateFloat) {
        animateFloat.animateTo(targetValue = 1f, animationSpec = tween(easing = LinearEasing))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sideSize = min(size.width, size.height)
        val diameter = sideSize - circleMargin
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 360f * animateFloat.value,
            useCenter = false,
            topLeft = Offset((size.width - diameter) / 2, (size.width - diameter) / 2),
            size = Size(diameter, diameter),
            style = Stroke(figureWidth),
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
    val color = MaterialTheme.colors.secondary
    val figureWidth = dimensionResource(id = R.dimen.figure_width).value
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.cross_padding))
    ) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(animVal.value * size.width, animVal.value * size.height),
            strokeWidth = figureWidth
        )
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(size.width - (animVal2.value * size.width), animVal2.value * size.height),
            strokeWidth = figureWidth
        )
    }
}

