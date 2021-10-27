package fr.tb_lab.model

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.tb_lab.model.parser.scan

class Cell {
    var content by mutableStateOf("")

    val tokenizedContent by derivedStateOf { scan(content) }
}