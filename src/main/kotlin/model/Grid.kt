package fr.tb_lab.model

class Grid(gridSize: Int) : List<List<Cell>> by List(gridSize, { List(gridSize) { Cell() } }) {
    val alphaHeader = AlphabeticalHeaderList(size)

    fun getCellFromStringCoordinates(coordinates: String): Result<Cell> {
        return if (coordinates.matches(matchCellCoordinate)) {
            val splitId = coordinates.indexOfFirst(Char::isDigit)
            val abscissa = coordinates.substring(0 until splitId)
            val ordinate = coordinates.substring(splitId until coordinates.length)
            Result.success(this[alphaHeader.indexOf(abscissa)][ordinate.toInt() - 1])
        } else Result.failure(IllegalArgumentException("Invalid coordinate"))
    }

    companion object {
        val matchCellCoordinate = "^[A-Z]+[1-9]+\$".toRegex()
    }
}