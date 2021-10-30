package fr.tb_lab.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Grid(private val list: List<List<Cell>>) : List<List<Cell>> by list {
    @Transient
    val alphaHeader = AlphabeticalHeaderList(size)

    constructor(gridSize: Int) : this(List(gridSize) { List(gridSize) { Cell() } })

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