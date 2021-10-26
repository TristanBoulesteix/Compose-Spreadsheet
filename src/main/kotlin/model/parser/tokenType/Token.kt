package fr.tb_lab.model.parser.tokenType

sealed class Token<T : Any>(val symbol: T) {
    companion object {
        fun getTokenFromSymbol(token: Char) = when (token) {
            '+' -> Add
            '-' -> Sub
            '*' -> Mul
            '/' -> Div
            '^' -> Pow
            '(' -> ParLeft
            ')' -> ParRight
            else -> Value(token.toString())
        }
    }

    override fun toString(): String {
        return "(Expr:$symbol, Token:${this::class.simpleName})"
    }
}

object ParLeft : Token<Char>('(')

object ParRight : Token<Char>(')')

class Value(content: Double) : Token<Double>(content) {
    constructor(content: String) : this(content.toDouble())
}

typealias TokenizedExpression = List<Token<*>>

typealias MutableTokenizedExpression = MutableList<Token<*>>