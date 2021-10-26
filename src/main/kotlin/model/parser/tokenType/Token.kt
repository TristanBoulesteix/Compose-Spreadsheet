package fr.tb_lab.model.parser.tokenType

import kotlin.reflect.KClass

sealed class Token<T : Any>(val symbol: T) {
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        private val objectTokens by lazy {
            buildSet {
                addAll(Token::class.sealedSubclasses.mapNotNull(KClass<out Token<*>>::objectInstance))
                addAll(AlgebraicToken::class.sealedSubclasses.mapNotNull(KClass<out Token<*>>::objectInstance))
            }
        }

        fun getTokenFromSymbol(token: Char) = objectTokens.find { it.symbol == token } ?: Value(token.toString())
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