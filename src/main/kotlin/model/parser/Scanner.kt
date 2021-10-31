package fr.tb_lab.model.parser

import fr.tb_lab.model.parser.tokenType.*

/**
 * Parse a string expression to convert it to a [TokenizedExpression].
 *
 * @param expression The expression to scan
 */
@OptIn(ExperimentalStdlibApi::class)
fun scan(expression: String): TokenizedExpression {
    val stringBuilder = StringBuilder()

    // If the expression is empty, there is no token
    if (expression.isBlank()) return emptyList()

    return buildList {
        // We iterate through each character to convert them into Token
        expression.trim().forEachIndexed character@{ index, char ->
            if (char.isLetter() && (stringBuilder.isEmpty() || stringBuilder.matches("^[A-Z]+\$".toRegex()))) {
                // if
                stringBuilder.append(char)
                return@character
            }

            val token = if (!char.isWhitespace()) Token.getTokenFromSymbol(char) else return@character

            if (token !is Value) {
                if (index == 0) {
                    when (token) {
                        is Add, is Sub -> {
                            stringBuilder.append(char)
                        }
                        is ParLeft -> add(ParLeft)
                        else -> return listOf(InvalidValue)
                    }
                } else {
                    when {
                        stringBuilder.isNotBlank() -> when {
                            token is ParLeft && stringBuilder.last { !it.isWhitespace() } == '-' -> {
                                add(Value(-1.0))
                                add(Mul)
                            }
                            token is ParLeft -> {
                                add(valueOf(stringBuilder.toString()))
                                stringBuilder.clear()
                                add(Mul)
                                add(token)
                                return@character
                            }
                            token is Dot -> {
                                stringBuilder.append(char)
                                return@character
                            }
                            else -> add(valueOf(stringBuilder.toString()))
                        }
                        token is Sub && last() is ParLeft -> {
                            stringBuilder.append(char)
                            return@character
                        }
                        token is ParLeft && isNotEmpty() && last() is ParRight -> add(Mul)
                        token is Dot -> return listOf(InvalidValue)
                    }

                    add(token)
                    stringBuilder.clear()
                }
            } else stringBuilder.append(char)
        }

        if (stringBuilder.isNotEmpty()) {
            add(valueOf(stringBuilder.toString()))
        }
    }
}