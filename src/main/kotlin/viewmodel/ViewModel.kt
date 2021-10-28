package fr.tb_lab.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import fr.tb_lab.model.Cell
import fr.tb_lab.model.EmptyValue
import fr.tb_lab.model.Grid
import fr.tb_lab.model.parser.evaluateCell

class ViewModel {
    private var selectedCellCord by mutableStateOf(0 to 0)

    val focusRequesterForInputFormula = FocusRequester()

    val grid = Grid(GRID_SIZE)

    val calculatedGrid = grid.map { row ->
        row.map { cell ->
            derivedStateOf {
                val result = evaluateCell(cell.tokenizedContent, grid, cell)

                if (result.isFailure) when (result.exceptionOrNull()) {
                    is EmptyValue -> ""
                    else -> TODO()
                } else result.getOrThrow().toString()
            }
        }
    }

    val selectedCell: Cell
        get() {
            val (x, y) = selectedCellCord
            return grid[x][y]
        }

    val cellInputText by derivedStateOf { selectedCell.content }

    fun setInputText(input: String) {
        selectedCell.content = input
    }

    fun setCellSelectedAt(x: Int, y: Int) {
        selectedCellCord = x to y
        focusRequesterForInputFormula.requestFocus()
    }

    private companion object {
        const val GRID_SIZE = 30
    }
}