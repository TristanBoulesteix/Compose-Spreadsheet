package fr.tb_lab.model.parser

import fr.tb_lab.model.Cell
import fr.tb_lab.model.Grid
import kotlin.math.pow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ScannerKtTest {
    private lateinit var testGrid: Grid

    private fun parseExpression(expression: String, cellToParse: Cell = testGrid.first().first()) =
        evaluateCell(scan(expression), testGrid, setOf(cellToParse))

    @BeforeTest
    fun initGrid() {
        testGrid = Grid(10)
    }

    @Test
    fun `scan expression with minus sign at the beginning and strange indent`() {
        val expression = " -(  2 + 8) - 2^(12*3)"
        val expectedResult = -(2 + 8) - 2.0.pow(12 * 3)

        assertEquals(expectedResult, parseExpression(expression).getOrThrow())
    }

    @Test
    fun `scan simple expression`() {
        val expression = "5- 43*3+7"
        val expectedResult = 5.0 - 43 * 3 + 7

        assertEquals(expectedResult, parseExpression(expression).getOrThrow())
    }

    @Test
    fun `scan cell reference`() {
        val cell1Content = "43*2+6"
        val cell1ExpectedResult = 43.0 * 2 + 6

        val cell1Calculated =
            parseExpression(cell1Content, testGrid[0][0].also { it.content = cell1Content }).getOrThrow()

        assertEquals(cell1ExpectedResult, cell1Calculated)

        val cellRefContent = "a1*2"
        val cellRefExpectedContent = cell1Calculated * 2

        assertEquals(cellRefExpectedContent, parseExpression(cellRefContent, testGrid[3][3]).getOrThrow())
    }
}