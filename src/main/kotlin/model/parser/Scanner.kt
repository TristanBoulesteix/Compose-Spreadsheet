package fr.tb_lab.model.parser

import fr.tb_lab.model.parser.tokenType.*

@OptIn(ExperimentalStdlibApi::class)
fun scan(expression: String): TokenizedExpression {
    val stringBuilder = StringBuilder()

    if (expression.isBlank()) return emptyList()

    return buildList {
        expression.trim().forEachIndexed character@{ index, char ->
            if (char.isLetter() && (stringBuilder.isEmpty() || stringBuilder.matches("^[A-Z]+\$".toRegex()))) {
                stringBuilder.append(char)
                return@character
            }

            val token = if (!char.isWhitespace()) Token.getTokenFromSymbol(char) else return@character

            if (token !is Value) {
                if (index == 0) {
                    if (token is Add || token is Sub) {
                        stringBuilder.append(char)
                    } else if (token is Dot) {
                        return listOf(InvalidValue)
                    }
                } else {
                    when {
                        stringBuilder.isNotBlank() -> when {
                            token is ParLeft && stringBuilder.last { !it.isWhitespace() } == '-' -> {
                                add(Value(-1.0))
                                add(Mul)
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