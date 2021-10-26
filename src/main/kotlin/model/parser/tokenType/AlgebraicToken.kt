package fr.tb_lab.model.parser.tokenType

import kotlin.math.pow

sealed class AlgebraicToken(symbol: Char, val op: Double.(Double) -> Double) : Token<Char>(symbol)

object Add : AlgebraicToken('+', Double::plus)

object Sub : AlgebraicToken('-', Double::minus)

object Div : AlgebraicToken('/', Double::div)

object Mul : AlgebraicToken('*', Double::times)

object Pow : AlgebraicToken('^', Double::pow)