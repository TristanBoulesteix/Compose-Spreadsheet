package fr.tb_lab.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import fr.tb_lab.model.Cell

class ViewModel {
    private var selectedCellCord by mutableStateOf(0 to 0)

    val grid =
        List(GRID_SIZE) { x -> List(GRID_SIZE) { y -> Cell("$x $y", x, y) }.toMutableStateList() }.toMutableStateList()

    val selectedCell: Cell
        get() {
            val (x, y) = selectedCellCord
            return grid[x][y]
        }

    var cellInputText by mutableStateOf("")
        private set

    fun setInputText(input: String) {
        cellInputText = input
    }

    fun setCellSelected(cell: Cell) {
        selectedCellCord = cell.x to cell.y
    }

    private companion object {
        const val GRID_SIZE = 30
    }
}