package fr.tb_lab.model

/**
 * Returned when there is no value in the expression
 */
class EmptyValue : Throwable()

/**
 * Returned when a cell reference another cell that reference itself directly or indirectly
 */
class RecursionError: Throwable()

/**
 * Returned when the expression is invalid
 */
class InvalidSymbolError: Throwable()