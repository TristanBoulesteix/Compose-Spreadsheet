package fr.tb_lab.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar

@Composable
fun FrameWindowScope.Menu() = MenuBar {
    Menu("File", enabled = false) {

    }
}