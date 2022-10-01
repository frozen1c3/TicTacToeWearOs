package dmitry.molchanov.tictactoe.presentation.game.model

data class Cell(
    val cellType: CellType,
    val playerType: PlayerType? = null,
    val clickedStatus: ClickedStatus = ClickedStatus.CLICKABLE,
)