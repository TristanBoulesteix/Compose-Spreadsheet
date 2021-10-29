package fr.tb_lab.model.parser

import fr.tb_lab.model.*
import fr.tb_lab.model.parser.tokenType.*
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class)
tailrec fun evaluateCell(
    tokenizedExpression: TokenizedExpression,
    grid: Grid,
    ignoredCells: Set<Cell>,
    isSubCell: Boolean = false
): Result<Double> {
    return when {
        tokenizedExpression.isEmpty() -> if (isSubCell) Result.success(.0) else Result.failure(EmptyValue())
        tokenizedExpression.size == 1 -> when (val firstToken = tokenizedExpression.first()) {
            is Value -> Result.success(firstToken.symbol)
            is CellValue -> {
                val cell = grid.getCellFromStringCoordinates(firstToken.symbol).getOrElse { return Result.failure(it) }

                if (cell !in ignoredCells) {
                    val cellToIgnore = ignoredCells + cell

                    evaluateCell(cell.tokenizedContent, grid, cellToIgnore, isSubCell = true)
                } else Result.failure(RecursionError())
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
                return evaluateSimpleExpression(tokenizedExpression, grid, ignoredCells)
            }

            val evaluatedSimpleExpression =
                evaluateSimpleExpression(simpleExpression, grid, ignoredCells).getOrElse { return Result.failure(it) }

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

            evaluateCell(partiallyEvaluated, grid, ignoredCells)
        }
    }
}

private tailrec fun evaluateSimpleExpression(
    expression: TokenizedExpression,
    grid: Grid,
    ignoredCells: Set<Cell>
): Result<Double> {
    return if (expression.size == 1 && expression.first() is Value) {
        Result.success((expression.first() as Value).symbol)
    } else {
        val simpleExpressions: MutableTokenizedExpression = mutableListOf()

        val id = expression.indexOf(Pow)

        if (id != -1) {
            val (baseToken, expToken) = expression.getSurroundingTokenValue(id)
                .getOrElse { return Result.failure(InvalidSymbolError()) }

            // TODO Check values
            val base = evaluateValueToken(baseToken, grid, ignoredCells) { return Result.failure(it) }
            val exp = evaluateValueToken(expToken, grid, ignoredCells) { return Result.failure(it) }

            val calcExp = base.pow(exp)

            for (i in 0 until id - 1) {
                simpleExpressions += expression[i]
            }

            simpleExpressions += Value(calcExp)

            for (i in id + 2 until expression.size) {
                simpleExpressions += expression[i]
            }

            evaluateSimpleExpression(simpleExpressions, grid, ignoredCells)
        } else {
            expression.evaluateArithmeticExpression(Mul, Div, setOf(Add to Sub), grid, ignoredCells)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private tailrec fun TokenizedExpression.evaluateArithmeticExpression(
    firstOperator: ArithmeticToken,
    secondOperator: ArithmeticToken,
    nextPairOperator: Set<Pair<ArithmeticToken, ArithmeticToken>>,
    grid: Grid,
    ignoredCells: Set<Cell>
): Result<Double> {
    val idOperator1 = indexOf(firstOperator)
    val idOperator2 = indexOf(secondOperator)

    val idComputed = if (idOperator1 >= 0 && idOperator2 >= 0) idOperator1.coerceAtMost(idOperator2)
    else idOperator1.coerceAtLeast(idOperator2)

    return if (idComputed != -1) {
        val (firstValueToken, secondValueToken) = getSurroundingTokenValue(idComputed).getOrNull()
            ?: return Result.failure(InvalidSymbolError())

        val firstValue = evaluateValueToken(firstValueToken, grid, ignoredCells) { return Result.failure(it) }

        val secondValue = evaluateValueToken(secondValueToken, grid, ignoredCells) { return Result.failure(it) }

        val op = if (idComputed == idOperator1) firstOperator.op else secondOperator.op

        val computed = firstValue.op(secondValue)

        val simpleExpression: TokenizedExpression = buildList {
            for (i in 0 until idComputed - 1) {
                add(this@evaluateArithmeticExpression[i])
            }

            add(Value(computed))

            for (i in idComputed + 2 until this@evaluateArithmeticExpression.size) {
                add(this@evaluateArithmeticExpression[i])
            }
        }

        evaluateSimpleExpression(simpleExpression, grid, ignoredCells)
    } else {
        if (nextPairOperator.isNotEmpty()) {
            val operator = nextPairOperator.first()
            val nextOperators = nextPairOperator - operator

            this.evaluateArithmeticExpression(operator.first, operator.second, nextOperators, grid, ignoredCells)
        } else Result.success((first() as Value).symbol) // TODO
    }
}

private inline fun evaluateValueToken(
    firstValueToken: ValueToken<*>,
    grid: Grid,
    ignoredCells: Set<Cell>,
    onFailure: (Throwable) -> Nothing
) = if (firstValueToken is CellValue) {
    val cellToEvaluate =
        grid.getCellFromStringCoordinates(firstValueToken.symbol).getOrElse(onFailure)

    evaluateCell(
        cellToEvaluate.tokenizedContent,
        grid,
        ignoredCells + cellToEvaluate,
        isSubCell = true
    ).getOrElse { if (it is EmptyValue) return .0 else onFailure(it) }
} else (firstValueToken as Value).symbol