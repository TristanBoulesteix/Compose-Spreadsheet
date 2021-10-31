package fr.tb_lab.model.parser

import fr.tb_lab.model.*
import fr.tb_lab.model.parser.tokenType.*
import kotlin.math.pow

/**
 * Entry point of the parser. Parse a [TokenizedExpression] to a [Double].
 *
 * @return A [Result] object with the double data or an exception if the expression was wrong or if an error occurred
 */
fun evaluateCell(
    tokenizedExpression: TokenizedExpression,
    grid: Grid,
    ignoredCells: Set<Cell>
) = try {
    evaluateCellExpression(tokenizedExpression, grid, ignoredCells)
} catch (t: Throwable) {
    Result.failure(t)
}

/**
 * Iterate through the [TokenizedExpression] to parse it.
 */
@OptIn(ExperimentalStdlibApi::class)
private tailrec fun evaluateCellExpression(
    tokenizedExpression: TokenizedExpression,
    grid: Grid,
    ignoredCells: Set<Cell>,
    isSubCell: Boolean = false
): Result<Double> {
    return when {
        // If the expression is empty we return from the function immediately
        tokenizedExpression.isEmpty() -> if (isSubCell) Result.success(.0) else Result.failure(EmptyValue())
        // if there is only one token in the expression, we calculate it without iteration to improve performances
        tokenizedExpression.size == 1 -> when (val firstToken = tokenizedExpression.first()) {
            is Value -> Result.success(firstToken.symbol) // If the token is a value, we return it immediately without calculation required
            is CellValue -> {
                // If the token is a cell reference, we calculate the value of it to convert it into a Value
                val cell = grid.getCellFromStringCoordinates(firstToken.symbol).getOrElse { return Result.failure(it) }

                if (cell !in ignoredCells) {
                    val cellToIgnore = ignoredCells + cell

                    evaluateCellExpression(cell.tokenizedContent, grid, cellToIgnore, isSubCell = true)
                } else Result.failure(RecursionError()) // The cell reference is recursive
            }
            else -> Result.failure(InvalidSymbolError()) // If the token is any other character, the input is wrong
        }
        tokenizedExpression.contains(InvalidValue) -> Result.failure(InvalidSymbolError()) // If the expression contains any wrong parsed value, the expression is wrong
        else -> {
            // First, we check if the expression has any parenthesis in it. If the is, we calculate the content between the two parenthesis first.
            val simpleExpression: MutableTokenizedExpression = mutableListOf()
            val id = tokenizedExpression.lastIndexOf(ParLeft)

            var matchingRPAR = -1

            if (id >= 0) {
                // If there is an opening parenthesis, we search for the corresponding closing parenthesis
                for (i in (id + 1) until tokenizedExpression.size) {
                    if (tokenizedExpression[i] is ParRight) {
                        matchingRPAR = i
                        break
                    } else simpleExpression += tokenizedExpression[i] // We store the tokens between the parenthesis to calculate them
                }
            } else {
                // If there is no parenthesis, we evaluate the expression normally
                return evaluateSimpleExpression(tokenizedExpression, grid, ignoredCells)
            }

            // The content between two parenthesis should not be empty
            if (simpleExpression.isEmpty())
                return Result.failure(InvalidSymbolError())

            // We evaluate the content between the parenthesis
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

            // We call recursively the function to check if there is not any parenthesis remaining
            evaluateCellExpression(partiallyEvaluated, grid, ignoredCells)
        }
    }
}

private tailrec fun evaluateSimpleExpression(
    expression: TokenizedExpression,
    grid: Grid,
    ignoredCells: Set<Cell>
): Result<Double> {
    return if (expression.size == 1 && expression.first() is Value) {
        // If there is only one token in the list, and it's a value, no need to evaluate it. we simply return it
        Result.success((expression.first() as Value).symbol)
    } else {
        val simpleExpressions: MutableTokenizedExpression = mutableListOf()

        // We check if the expression has a pow operator (^). This operator has a higher priority than any other
        val id = expression.indexOf(Pow)

        if (id != -1) {
            // if there is a pow operator.
            val (baseToken, expToken) = expression.getSurroundingTokenValue(id)
                .getOrElse { return Result.failure(InvalidSymbolError()) }

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

            // We call recursively the function with the new evaluated expression to check if there is not any pow operator remaining
            evaluateSimpleExpression(simpleExpressions, grid, ignoredCells)
        } else {
            // If there is not any pow operator, we evaluate the other operator in the expression
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
    // We evaluate each group of operator by arithmetic priority order (* and / -> + and -)
    val idOperator1 = indexOf(firstOperator)
    val idOperator2 = indexOf(secondOperator)

    val idComputed = if (idOperator1 >= 0 && idOperator2 >= 0) idOperator1.coerceAtMost(idOperator2)
    else idOperator1.coerceAtLeast(idOperator2)

    return if (idComputed != -1) {
        // if there is an operator to evaluate
        val (firstValueToken, secondValueToken) = getSurroundingTokenValue(idComputed).getOrNull()
            ?: return Result.failure(InvalidSymbolError())

        val firstValue = evaluateValueToken(firstValueToken, grid, ignoredCells) { return Result.failure(it) }

        val secondValue = evaluateValueToken(secondValueToken, grid, ignoredCells) { return Result.failure(it) }

        // We execute the operation corresponding to the detected operator
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

        // We can now evaluate the new expression
        evaluateSimpleExpression(simpleExpression, grid, ignoredCells)
    } else {
        if (nextPairOperator.isNotEmpty()) {
            // If there is still a group of operator left, we call the same function again to evaluate the expression with those operators
            val operator = nextPairOperator.first()
            val nextOperators = nextPairOperator - operator

            this.evaluateArithmeticExpression(operator.first, operator.second, nextOperators, grid, ignoredCells)
        } else runCatching { (first() as Value).symbol } // Once everything is evaluated, we return the result of the expression
    }
}

/**
 * Convert a [ValueToken] to its corresponding [Double] value or call the fallback function in case of failure.
 *
 * @param firstValueToken The token to convert
 * @param grid The grid where the token is stored. It is used if the token is a [CellValue] to evaluate the value of its reference
 * @param ignoredCells The cell to ignore to avoid circular references
 * @param onFailure A fallback function in case of failure that returns [Nothing]. Its main purpose is non-local return in the parent function
 */
private inline fun evaluateValueToken(
    firstValueToken: ValueToken<*>,
    grid: Grid,
    ignoredCells: Set<Cell>,
    onFailure: (Throwable) -> Nothing
) = if (firstValueToken is CellValue) {
    val cellToEvaluate =
        grid.getCellFromStringCoordinates(firstValueToken.symbol).getOrElse(onFailure)

    evaluateCellExpression(
        cellToEvaluate.tokenizedContent,
        grid,
        ignoredCells + cellToEvaluate,
        isSubCell = true
    ).getOrElse { if (it is EmptyValue) return .0 else onFailure(it) }
} else (firstValueToken as Value).symbol