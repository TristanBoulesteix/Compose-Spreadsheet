package fr.tb_lab.model.parser

import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals

internal class ScannerKtTest {
    @Test
    fun parseExpression() {
        val expression = "2 ^2+5*6-(2-4)"
        val expectedResult = 36.0

        assertEquals(expectedResult, parseExpression(expression))
    }

    @Test
    fun `parse expression with minus sign at the beginning and strange indent`() {
        val expression = " -(  2 + 8) - 2^(12*3)"
        val expectedResult = -(2 + 8) - 2.0.pow(12 * 3)

        assertEquals(expectedResult, parseExpression(expression))
    }

    @Test
    fun scanSub() {
        val a = "-(2 + 5)"

        assertEquals(-7.0, parseExpression(a))
    }
}