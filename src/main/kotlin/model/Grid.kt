package fr.tb_lab.model

class Grid(gridSize: Int) : List<List<Cell>> by List(gridSize, { List(gridSize) { Cell() } }) {
    val alphaHeader = AlphabeticalHeaderList(size)

    fun getCellFromStringCoordinates(coordinates: String): Cell = TODO()
}