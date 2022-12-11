package fr.tb_lab.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.tb_lab.model.Cell
import fr.tb_lab.model.Grid
import fr.tb_lab.view.components.LazyScrollableGrid

@Composable
fun Grid(
    gridCell: Grid,
    calculatedGrid: List<List<State<String>>>,
    selectedCell: Cell,
    setSelectedCell: (x: Int, y: Int) -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    val cellSize = remember { DpSize(width = 75.dp, height = 35.dp) }

    LazyScrollableGrid(
        modifier = Modifier.fillMaxSize().padding(5.dp),
        grid = gridCell,
        cellSize = cellSize
    ) { cell, rowIndex, columnIndex ->
        if (cell != selectedCell) {
            val calculatedContent by calculatedGrid[rowIndex][columnIndex]

            val cellTextColor = if (calculatedContent.toDoubleOrNull() != null) Color.Unspecified else Color.Red

            Text(
                text = calculatedContent,
                modifier = cellModifier(cellSize).cellSelector(setSelectedCell, rowIndex, columnIndex),
                color = cellTextColor,
                maxLines = 1
            )
        } else {
            Text(
                text = cell.content,
                modifier = cellModifier(
                    cellSize = cellSize,
                    isActive = true
                ).cellSelector(setSelectedCell, rowIndex, columnIndex)
            )
        }
    }
}

@Composable
private fun Modifier.cellSelector(
    setSelectedCell: (x: Int, y: Int) -> Unit,
    rowIndex: Int,
    columnIndex: Int
): Modifier = clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() }) {
    setSelectedCell(
        rowIndex,
        columnIndex
    )
}

@Stable
private fun cellModifier(
    cellSize: DpSize,
    isActive: Boolean = false
) = Modifier.size(cellSize).border(if (isActive) BorderStroke(3.dp, Color.Blue) else BorderStroke(1.dp, Color.Black))