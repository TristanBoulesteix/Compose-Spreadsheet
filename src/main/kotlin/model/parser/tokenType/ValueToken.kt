package fr.tb_lab.model.parser.tokenType

sealed class ValueToken<T>(value: T) : Token<T>(value)

class Value(content: Double) : ValueToken<Double>(content) {
    constructor(content: String) : this(content.toDouble())
}

object InvalidValue : ValueToken<Nothing?>(null)
