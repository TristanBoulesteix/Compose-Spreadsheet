package fr.tb_lab.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.tb_lab.model.Cell
import fr.tb_lab.view.components.LazyScrollableGrid

@Composable
fun InputFormula(contentText: String, setContentText: (String) -> Unit) = Box(modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
        value = contentText,
        onValueChange = setContentText,
        modifier = Modifier.padding(10.dp).fillMaxWidth()
    )
}

@Composable
fun Grid(
    gridCell: List<List<Cell>>,
    selectedCell: Cell,
    setSelectedCell: (Cell) -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    val cellSize = remember { DpSize(width = 60.dp, height = 30.dp) }

    LazyScrollableGrid(modifier = Modifier.fillMaxSize().padding(5.dp), grid = gridCell, cellSize = cellSize) {
        TextField(
            value = it.content,
            onValueChange = {},
            modifier = Modifier.size(cellSize)
                .border(width = 1.dp, color = if (it == selectedCell) Color.Blue else Color.Black)
                .clickable { setSelectedCell(it) }
        )
    }
}