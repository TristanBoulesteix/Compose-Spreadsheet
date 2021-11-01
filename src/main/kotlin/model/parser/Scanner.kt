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
            if (char.isLetter() && (stringBuilder.isEmpty() || stringBuilder.matches("^[A-Za-z]+\$".toRegex()))) {
                // if the char can be part of a cell name, we add it to the StringBuilder and then continue the loop
                stringBuilder.append(char)
                return@character
            }

            // We convert the current char to its corresponding token
            val token = if (!char.isWhitespace()) Token.getTokenFromSymbol(char) else return@character

            if (token !is Value) {
                if (index == 0) {
                    when (token) {
                        // If the first token is a plus, minus or parleft symbol we treat them as unary operator
                        is Add, is Sub -> {
                            stringBuilder.append(char)
                        }
                        is ParLeft -> add(ParLeft)
                        else -> return listOf(InvalidValue) // If the symbol is none of the ones cited above, the expression is a syntax error
                    }
                } else {
                    when {
                        stringBuilder.isNotBlank() -> when {
                            // If the expression looks like "-(4+1)" we need to convert it to "-1 * (4+1)" to allow the parser to translate it easily
                            token is ParLeft && stringBuilder.last { !it.isWhitespace() } == '-' -> {
                                add(Value(-1.0))
                                add(Mul)
                            }
                            // 2(6+2) is like 2*(6+2)
                            token is ParLeft -> {
                                add(valueOf(stringBuilder.toString()))
                                stringBuilder.clear()
                                add(Mul)
                                add(token)
                                return@character
                            }
                            // Dot is a part of a value even though it's not a number
                            token is Dot -> {
                                stringBuilder.append(char)
                                return@character
                            }
                            // Everything else is a normal value. We create a value token from the stringbuilder
                            else -> add(valueOf(stringBuilder.toString()))
                        }
                        // If the first token after an opening parenthesis is a minus symbol, we treat this operator as unary
                        token is Sub && last() is ParLeft -> {
                            stringBuilder.append(char)
                            return@character
                        }
                        // (2+4)(6+2) is like (2+4)*(6+2)
                        token is ParLeft && isNotEmpty() && last() is ParRight -> add(Mul)
                        // A random dot is an error
                        token is Dot -> return listOf(InvalidValue)
                    }

                    add(token)
                    stringBuilder.clear()
                }
            } else stringBuilder.append(char) // If the token is a regular value, we add it to the stringbuilder to create a complete number when the next token is an arithmetic symbol
        }

        // We add the last value by converting the stringbuilder to a valueToken
        if (stringBuilder.isNotEmpty()) {
            add(valueOf(stringBuilder.toString()))
        }
    }
}