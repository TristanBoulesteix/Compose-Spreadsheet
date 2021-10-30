package fr.tb_lab.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import fr.tb_lab.model.*
import fr.tb_lab.model.parser.evaluateCell
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ViewModel {
    private var selectedCellCord by mutableStateOf(0 to 0)

    val focusRequesterForInputFormula = FocusRequester()

    var grid by mutableStateOf(Grid(GRID_SIZE))
        private set

    val calculatedGrid by derivedStateOf {
        grid.map { row ->
            row.map { cell ->
                derivedStateOf {
                    val result = evaluateCell(cell.tokenizedContent, grid, setOf(cell))

                    if (result.isFailure) when (result.exceptionOrNull()) {
                        is EmptyValue -> ""
                        is RecursionError -> "REC"
                        is InvalidSymbolError -> "SYNTAX"
                        else -> "Error"
                    } else result.getOrThrow().let { if (it.isNaN() || it.isInfinite()) "Math" else it.toString() }
                }
            }
        }
    }

    val selectedCell: Cell
        get() {
            val (x, y) = selectedCellCord
            return grid[x][y]
        }

    val cellInputText by derivedStateOf { selectedCell.content }

    var errorMessage: String? by mutableStateOf(null)

    fun setInputText(input: String) {
        selectedCell.content = input
    }

    fun setCellSelectedAt(x: Int, y: Int) {
        selectedCellCord = x to y
        focusRequesterForInputFormula.requestFocus()
    }

    fun exportGrid(path: Path) {
        try {
            val jsonGrid = Json.encodeToString(grid)
            path.writeText(jsonGrid)
        } catch (e: Throwable) {
            errorMessage = "An error has occurred. Unable to export grid. Please try again."
        }
    }

    fun importGrid(path: Path) {
        try {
            grid = Json.decodeFromString(path.readText())
        } catch (e: Throwable) {
            errorMessage = """An error has occurred. Unable to import grid. 
                |Please check that your file respect the right format.""".trimMargin()
        }
    }

    private companion object {
        const val GRID_SIZE = 30
    }
}