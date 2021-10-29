package fr.tb_lab.model.parser

import fr.tb_lab.model.*
import fr.tb_lab.model.parser.tokenType.*
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class)
tailrec fun evaluateCell(
    tokenizedExpression: TokenizedExpression,
    grid: Grid,
    vararg ignoredCell: Cell
): Result<Double> {
    return when {
        tokenizedExpression.isEmpty() -> Result.failure(EmptyValue())
        tokenizedExpression.size == 1 -> when (val firstToken = tokenizedExpression.first()) {
            is Value -> Result.success(firstToken.symbol)
            is CellValue -> {
                val resultCell = grid.getCellFromStringCoordinates(firstToken.symbol)
                if (resultCell.isSuccess) {
                    val cell = resultCell.getOrThrow()

                    if (cell !in ignoredCell) {
                        val cellToIgnore = arrayOf(*ignoredCell, cell)

                        evaluateCell(cell.tokenizedContent, grid, *cellToIgnore)
                    } else Result.failure(RecursionError())
                } else Result.failure(resultCell.exceptionOrNull()!!)
            }
            else -> Result.failure(InvalidSymbolError())
        }
        tokenizedExpression.contains(InvalidValue) -> Result.failure(InvalidSymbolError())
        else -> {
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

            val evaluatedSimpleExpression = evaluateSimpleExpression(simpleExpression).getOrNull() ?: TODO()

            val partiallyEvaluated = buildList {
                for (i in 0 until id) {
                    add(tokenizedExpression[i])
                }

                add(Value(evaluatedSimpleExpression))

                if (matchingRPAR != -1)
                    for (i in matchingRPAR + 1 until tokenizedExpression.size) {
                        add(tokenizedExpression[i])
                    }
            }

            evaluateCell(partiallyEvaluated, grid, *ignoredCell)
        }
    }
}

private tailrec fun evaluateSimpleExpression(expression: TokenizedExpression): Result<Double> {
    return if (expression.size == 1 && expression.first() is Value) {
        Result.success((expression.first() as Value).symbol)
    } else {
        val simpleExpressions: MutableTokenizedExpression = mutableListOf()

        val id = expression.indexOf(Pow)

        if (id != -1) {
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
): Result<Double> {
    val idOperator1 = indexOf(firstOperator)
    val idOperator2 = indexOf(secondOperator)

    val idComputed = if (idOperator1 >= 0 && idOperator2 >= 0) idOperator1.coerceAtMost(idOperator2)
    else idOperator1.coerceAtLeast(idOperator2)

    return if (idComputed != -1) {
        val (firstValueToken, secondValueToken) = getSurroundingTokenValue(idComputed).getOrNull()
            ?: return Result.failure(InvalidSymbolError())

        if(firstValueToken is CellValue) {
            
        }

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
        } else Result.success((first() as Value).symbol) // TODO
    }
}