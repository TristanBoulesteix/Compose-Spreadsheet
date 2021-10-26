package fr.tb_lab.model.parser

import fr.tb_lab.model.parser.tokenType.*
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class)
fun scan(expression: String): TokenizedExpression {
    var stringBuilder = StringBuilder()

    if (expression.isBlank()) return emptyList()

    return buildList {
        expression.trim().forEachIndexed { index, char ->
            val token = if (!char.isWhitespace()) Token.getTokenFromSymbol(char) else return@forEachIndexed

            if (token !is Value) {
                if (index == 0 && (token is Add || token is Sub)) {
                    stringBuilder.append(char)
                } else {
                    if (stringBuilder.isNotEmpty()) {
                        if (token is ParLeft && stringBuilder.last { !it.isWhitespace() } == '-') {
                            add(Value(-1.0))
                            add(Mul)
                        } else {
                            add(Value(stringBuilder.toString()))
                        }
                    }

                    add(token)
                    stringBuilder = StringBuilder()
                }
            } else stringBuilder.append(char)
        }

        if (stringBuilder.isNotEmpty()) {
            add(Value(stringBuilder.toString()))
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
tailrec fun evaluate(tokenizedExpression: TokenizedExpression): Double? {
    if (tokenizedExpression.isEmpty()) return null

    if (tokenizedExpression.size == 1 && tokenizedExpression.first() is Value) {
        // TODO: Check that expression is really a value. If not throw exception
        return (tokenizedExpression.first() as Value).symbol
    }

    val simpleExpression: MutableTokenizedExpression = mutableListOf()
    val id = tokenizedExpression.lastIndexOf(ParLeft)

    var matchingRPAR = -1

    if (id >= 0) {
        for (i in (id + 1) until tokenizedExpression.size) {
            if (tokenizedExpression[i] is ParRight) {
                matchingRPAR = i
                break
            } else simpleExpression += tokenizedExpression[i]
        }
    } else {
        return evaluateSimpleExpression(tokenizedExpression)
    }

    val evaluatedSimpleExpression = evaluateSimpleExpression(simpleExpression)

    val partiallyEvaluated = buildList {
        for (i in 0 until id) {
            add(tokenizedExpression[i])
        }

        add(Value(evaluatedSimpleExpression.toString()))

        for (i in matchingRPAR + 1 until tokenizedExpression.size) {
            add(tokenizedExpression[i])
        }
    }

    return evaluate(partiallyEvaluated)
}

private tailrec fun evaluateSimpleExpression(expression: TokenizedExpression): Double {
    if (expression.size == 1 && expression.first() is Value) {
        // TODO: Check that expression is really a value. If not throw exception
        return (expression.first() as Value).symbol
    } else {
        val simpleExpressions: MutableTokenizedExpression = mutableListOf()

        val id = expression.indexOf(Pow)

        return if (id != -1) {
            // TODO Check values
            val base = (expression[id - 1] as Value).symbol
            val exp = (expression[id + 1] as Value).symbol

            val calcExp = base.pow(exp)

            for (i in 0 until id - 1) {
                simpleExpressions += expression[i]
            }

            simpleExpressions += Value(calcExp)

            for (i in id + 2 until expression.size) {
                simpleExpressions += expression[i]
            }

            evaluateSimpleExpression(simpleExpressions)
        } else {
            expression.evaluateAlgebraicExpression(Mul, Div, setOf(Add to Sub))
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private tailrec fun TokenizedExpression.evaluateAlgebraicExpression(
    firstOperator: AlgebraicToken,
    secondOperator: AlgebraicToken,
    nextPairOperator: Set<Pair<AlgebraicToken, AlgebraicToken>>
): Double {
    val idOperator1 = indexOf(firstOperator)
    val idOperator2 = indexOf(secondOperator)

    val idComputed = if (idOperator1 >= 0 && idOperator2 >= 0) idOperator1.coerceAtMost(idOperator2)
    else idOperator1.coerceAtLeast(idOperator2)

    return if (idComputed != -1) {
        // TODO Check values
        val firstValue = (this[idComputed - 1] as Value).symbol
        val secondValue = (this[idComputed + 1] as Value).symbol

        val op = if (idComputed == idOperator1) firstOperator.op else secondOperator.op

        val computed = firstValue.op(secondValue)

        val simpleExpression: TokenizedExpression = buildList {
            for (i in 0 until idComputed - 1) {
                add(this@evaluateAlgebraicExpression[i])
            }

            add(Value(computed))

            for (i in idComputed + 2 until this@evaluateAlgebraicExpression.size) {
                add(this@evaluateAlgebraicExpression[i])
            }
        }

        evaluateSimpleExpression(simpleExpression)
    } else {
        if (nextPairOperator.isNotEmpty()) {
            val operator = nextPairOperator.first()
            val nextOperators = nextPairOperator - operator

            this.evaluateAlgebraicExpression(operator.first, operator.second, nextOperators)
        } else (first() as Value).symbol // TODO
    }
}