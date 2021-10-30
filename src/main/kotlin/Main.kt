package fr.tb_lab

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import fr.tb_lab.view.ErrorDialog
import fr.tb_lab.view.Grid
import fr.tb_lab.view.InputFormula
import fr.tb_lab.view.Menu
import fr.tb_lab.viewmodel.ViewModel

fun main() = singleWindowApplication(
    title = "ComposeSheet",
    state = WindowState(position = WindowPosition.Aligned(Alignment.Center)),
) {
    val viewModel = remember(::ViewModel)

    MaterialTheme {
        viewModel.errorMessage?.let {
            ErrorDialog(title = "Error", message = it, onCloseRequest = { viewModel.errorMessage = null })
        }

        Menu(exportGrid = viewModel::exportGrid, importGrid = viewModel::importGrid)
        App(viewModel)
    }
}

@Composable
fun App(viewModel: ViewModel) = Column {
    InputFormula(
        contentText = viewModel.cellInputText,
        setContentText = viewModel::setInputText,
        focusRequester = viewModel.focusRequesterForInputFormula
    )
    Grid(
        gridCell = viewModel.grid,
        calculatedGrid = viewModel.calculatedGrid,
        selectedCell = viewModel.selectedCell,
        setSelectedCell = viewModel::setCellSelectedAt
    )
}

