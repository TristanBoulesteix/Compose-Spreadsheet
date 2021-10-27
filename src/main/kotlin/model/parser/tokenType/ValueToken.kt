package fr.tb_lab.model.parser.tokenType

sealed class ValueToken<T>(value: T) : Token<T>(value)

class Value(value: Double) : ValueToken<Double>(value) {
    constructor(value: String) : this(value.toDouble())
}

object InvalidValue : ValueToken<Nothing?>(null)