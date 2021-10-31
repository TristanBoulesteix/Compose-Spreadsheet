package fr.tb_lab.model.parser.tokenType

import kotlin.reflect.KClass

sealed class Token<T>(val symbol: T) {
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        private val objectTokens by lazy {
            buildSet {
                addAll(Token::class.sealedSubclasses.mapNotNull(KClass<out Token<*>>::objectInstance))
                addAll(ArithmeticToken::class.sealedSubclasses.mapNotNull(KClass<out Token<*>>::objectInstance))
            }
        }

        fun getTokenFromSymbol(token: Char) = objectTokens.find { it.symbol == token } ?: valueOf(token)
    }

    override fun toString() = "(Expr:$symbol, Token:${this::class.simpleName})"
}

object ParLeft : Token<Char>('(')

object ParRight : Token<Char>(')')

object Dot : Token<Char>('.')