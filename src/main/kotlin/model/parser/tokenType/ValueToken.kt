package fr.tb_lab.model.parser.tokenType

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
        value.matches("^[A-Z]+[1-9]+\$".toRegex()) -> CellValue(value)
        else -> InvalidValue
    }
}