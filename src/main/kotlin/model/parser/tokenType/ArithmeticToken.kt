package fr.tb_lab.model.parser.tokenType

import kotlin.math.pow

sealed class ArithmeticToken(symbol: Char, val op: Double.(Double) -> Double) : Token<Char>(symbol)

object Add : ArithmeticToken('+', Double::plus)

object Sub : ArithmeticToken('-', Double::minus)

object Div : ArithmeticToken('/', Double::div)

object Mul : ArithmeticToken('*', Double::times)

object Pow : ArithmeticToken('^', Double::pow)

object Mod : ArithmeticToken('%', Double::mod)