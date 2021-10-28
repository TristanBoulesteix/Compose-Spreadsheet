package fr.tb_lab.model.parser.tokenType

import fr.tb_lab.model.Grid

sealed class ValueToken<T>(value: T) : Token<T>(value)

class Value(value: Double) : ValueToken<Double>(value) {
    constructor(value: Char) : this(value.toString().toDouble())
}

object InvalidValue : ValueToken<Nothing?>(null)

class CellValue(cellCoordinate: String) : ValueToken<String>(cellCoordinate)

fun valueOf(value: String): ValueToken<*> {
    val doubleValue = value.toDoubleOrNull()

    return when {
        doubleValue != null -> Value(doubleValue)
        value.matches(Grid.matchCellCoordinate) -> CellValue(value)
        else -> InvalidValue
    }
}