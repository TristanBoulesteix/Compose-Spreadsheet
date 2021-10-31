package fr.tb_lab.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import java.io.File
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter

private const val extension = "json"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.Menu(exportGrid: (Path) -> Unit, importGrid: (Path) -> Unit) = MenuBar {
    Menu("File", mnemonic = 'f') {
        Item("Export grid", shortcut = KeyShortcut(Key.S, ctrl = true)) {
            JFileChooser().apply {
                dialogTitle = "Save as"
                fileFilter = FileNameExtensionFilter("JSON file", extension)
                if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    val fileToSave =
                        if (selectedFile.extension == extension) selectedFile else File("${this.selectedFile.parentFile.absolutePath}${File.separator}${this.selectedFile.nameWithoutExtension}.$extension")

                    if (!fileToSave.exists() || confirmOverrideFile()) {
                        exportGrid(fileToSave.toPath())
                    }
                }
            }
        }

        Item("Import grid", shortcut = KeyShortcut(Key.O, ctrl = true)) {
            JFileChooser().apply {
                dialogTitle = "Import grid"
                fileFilter = FileNameExtensionFilter("JSON file", extension)
                if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION && !this.selectedFile.isDirectory && this.selectedFile.exists()) {
                    importGrid(selectedFile.toPath())
                }
            }
        }
    }
}

/**
 * Show a JOption pane to ask the user if he wants to override an existing file by a new one
 */
private fun confirmOverrideFile() = JOptionPane.showConfirmDialog(
    null,
    "A file with the same name already exists. Do you want to override it?",
    "File already exists",
    JOptionPane.YES_NO_OPTION
) == JOptionPane.YES_OPTION