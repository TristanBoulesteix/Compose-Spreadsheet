package fr.tb_lab.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.tb_lab.model.Cell
import fr.tb_lab.view.components.LazyScrollableGrid

@Composable
fun Grid(
    gridCell: List<List<Cell>>,
    selectedCell: Cell,
    setSelectedCell: (x: Int, y: Int) -> Unit,
    setContentText: (String) -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    val cellSize = remember { DpSize(width = 60.dp, height = 30.dp) }

    LazyScrollableGrid(
        modifier = Modifier.fillMaxSize().padding(5.dp),
        grid = gridCell,
        cellSize = cellSize
    ) { cell, rowIndex, columnIndex ->
        if (cell != selectedCell) {
            Text(
                text = cell.calculatedContent,
                modifier = cellModifier(cellSize).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    setSelectedCell(
                        rowIndex,
                        columnIndex
                    )
                }
            )
        } else {
            BasicTextField(
                value = cell.content,
                onValueChange = setContentText,
                modifier = cellModifier(
                    cellSize = cellSize,
                    isActive = cell == selectedCell
                ),
                singleLine = true
            )
        }
    }
}

@Stable
private fun cellModifier(
    cellSize: DpSize,
    isActive: Boolean = false
) = Modifier.size(cellSize).border(if (isActive) BorderStroke(3.dp, Color.Blue) else BorderStroke(1.dp, Color.Black))