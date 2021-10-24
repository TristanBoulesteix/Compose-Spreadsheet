package fr.tb_lab.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.tb_lab.model.Cell
import fr.tb_lab.view.components.LazyScrollableGrid

@Composable
fun InputFormula(contentText: String, focusRequester: FocusRequester, setContentText: (String) -> Unit) =
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = contentText,
            onValueChange = setContentText,
            modifier = Modifier.padding(10.dp).fillMaxWidth().focusRequester(focusRequester)
        )
    }

@Composable
fun Grid(
    gridCell: List<List<Cell>>,
    selectedCell: Cell,
    setSelectedCell: (x: Int, y: Int) -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    val cellSize = remember { DpSize(width = 60.dp, height = 30.dp) }

    LazyScrollableGrid(
        modifier = Modifier.fillMaxSize().padding(5.dp),
        grid = gridCell,
        cellSize = cellSize
    ) { cell, rowIndex, columnIndex ->
        if (cell != selectedCell) {
            Text(
                text = cell.content,
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
            var content by remember { mutableStateOf(cell.content) }

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                modifier = cellModifier(
                    cellSize = cellSize,
                    isActive = cell == selectedCell
                )
            )
        }
    }
}

@Stable
private fun cellModifier(
    cellSize: DpSize,
    isActive: Boolean = false
) = Modifier.size(cellSize).border(width = 1.dp, color = if (isActive) Color.Blue else Color.Black)