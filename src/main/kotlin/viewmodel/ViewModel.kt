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
    /**
     * Coordinates of the current selected cell
     */
    private var selectedCellCord by mutableStateOf(0 to 0)

    /**
     * [FocusRequester] of the input formula text field
     */
    val focusRequesterForInputFormula = FocusRequester()

    /**
     * The grid that contains all cell data
     */
    var grid by mutableStateOf(Grid(GRID_SIZE))
        private set

    /**
     * The values corresponding to each cell of the grid. It is stored separately to cache data and improve performances
     */
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

    /**
     * Public getter to obtains to current selected [Cell] object
     */
    val selectedCell: Cell
        get() {
            val (x, y) = selectedCellCord
            return grid[x][y]
        }

    /**
     * The content of the current cell to be displayed
     */
    val cellInputText by derivedStateOf { selectedCell.content }

    /**
     * Error message to show if not null
     */
    var errorMessage: String? by mutableStateOf(null)

    /**
     * Set the text of the cell
     *
     * @param input The [String] value to set
     */
    fun setInputText(input: String) {
        selectedCell.content = input
    }

    /**
     * Select a cell with its coordinates
     *
     * @param x The x-axis coordinate
     * @param y the y-axis coordinate
     */
    fun setCellSelectedAt(x: Int, y: Int) {
        selectedCellCord = x to y
        focusRequesterForInputFormula.requestFocus()
    }

    /**
     * Export the current grid data to json and save it
     *
     * @param path The destination path to save the Json
     */
    fun exportGrid(path: Path) {
        try {
            val jsonGrid = Json.encodeToString(grid)
            path.writeText(jsonGrid)
        } catch (e: Throwable) {
            errorMessage = "An error has occurred. Unable to export grid. Please try again."
        }
    }

    /**
     * Import a json to replace the current grid
     *
     * @param path The path to get the json
     */
    fun importGrid(path: Path) {
        try {
            val newGrid: Grid = Json.decodeFromString(path.readText())
            grid = newGrid.takeIf { grid -> grid.size == GRID_SIZE && grid.all { it.size == GRID_SIZE } }
                ?: throw IllegalArgumentException("Invalid size of grid")
        } catch (e: Throwable) {
            errorMessage = """An error has occurred. Unable to import grid. 
                |Please check that your file respect the right format.""".trimMargin()
        }
    }

    private companion object {
        const val GRID_SIZE = 40
    }
}