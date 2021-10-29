package fr.tb_lab.model

class Grid(gridSize: Int) : List<List<Cell>> by List(gridSize, { List(gridSize) { Cell() } }) {
    val alphaHeader = AlphabeticalHeaderList(size)

    fun getCellFromStringCoordinates(coordinates: String): Result<Cell> {
        return if (coordinates.matches(matchCellCoordinate)) {
            val splitId = coordinates.indexOfFirst(Char::isDigit)
            val columnId = coordinates.substring(0 until splitId)
            val rowId = coordinates.substring(splitId until coordinates.length)
            Result.success(this[rowId.toInt() - 1][alphaHeader.indexOf(columnId)])
        } else Result.failure(IllegalArgumentException("Invalid coordinate"))
    }

    companion object {
        val matchCellCoordinate = "^[A-Z]+[1-9]+\$".toRegex()
    }
}