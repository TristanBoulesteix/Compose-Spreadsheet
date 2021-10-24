package fr.tb_lab.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.focus.FocusRequester
import fr.tb_lab.model.Cell

class ViewModel {
    private var selectedCellCord by mutableStateOf(0 to 0)

    val focusRequesterForInputFormula = FocusRequester()

    val grid = List(GRID_SIZE) { List(GRID_SIZE) { Cell() }.toMutableStateList() }.toMutableStateList()

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

    fun setCellSelectedAt(x: Int, y: Int) {
        selectedCellCord = x to y
        focusRequesterForInputFormula.requestFocus()
    }

    private companion object {
        const val GRID_SIZE = 30
    }
}