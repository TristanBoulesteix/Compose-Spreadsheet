package fr.tb_lab.model.parser.tokenType

import fr.tb_lab.model.InvalidSymbolError

typealias TokenizedExpression = List<Token<*>>

typealias MutableTokenizedExpression = MutableList<Token<*>>

fun TokenizedExpression.getSurroundingTokenValue(index: Int): Result<SurroundingToken> =
    if (isEmpty() || index !in 1 until size - 1)
        Result.failure(InvalidSymbolError())
    else {
        val previousToken = this[index - 1] as? ValueToken
        val nextToken = this[index + 1] as? ValueToken

        when {
            previousToken == null || nextToken == null || previousToken is InvalidValue || nextToken is InvalidValue -> Result.failure(
                InvalidSymbolError()
            )
            else -> Result.success(SurroundingToken(previousToken, nextToken))
        }
    }

data class SurroundingToken(val previousToken: ValueToken<*>, val nexToken: ValueToken<*>)