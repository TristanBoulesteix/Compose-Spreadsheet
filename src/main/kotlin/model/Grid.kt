package fr.tb_lab.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Representation of a grid. It is a 2-dimensional list of [Cell]s. There is a private constructor for the serialization
 * only and a public one for regular usage that take a size of the grid as argument
 */
@Serializable
class Grid private constructor(private val list: List<List<Cell>>) : List<List<Cell>> by list {
    @Transient
    val alphaHeader = AlphabeticalHeaderList(size)

    /**
     * Representation of a grid. It is a 2-dimensional list of [Cell]s.
     * @param gridSize The size of the grid
     */
    constructor(gridSize: Int) : this(List(gridSize) { List(gridSize) { Cell() } })

    fun getCellFromStringCoordinates(coordinates: String): Result<Cell> {
        return if (coordinates.matches(matchCellCoordinate)) {
            val splitId = coordinates.indexOfFirst(Char::isDigit)
            val columnId = coordinates.substring(0 until splitId)
            val rowId = coordinates.substring(splitId until coordinates.length)
            Result.success(this[rowId.toInt() - 1][alphaHeader.indexOf(columnId.uppercase())])
        } else Result.failure(IllegalArgumentException("Invalid coordinate"))
    }

    companion object {
        val matchCellCoordinate = "^[A-Za-z]+[1-9]+\$".toRegex()
    }
}