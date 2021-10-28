package fr.tb_lab.model

import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

internal class GridTest {
    private val grid = Grid(10)

    @BeforeTest
    fun initGrid() {
        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, cell ->
                cell.content = "$rowIndex$columnIndex"
            }
        }
    }

    @Test
    fun `check coordinates of cell`() {
        val cell = grid[2][2]

        assertEquals("22", cell.content)
    }

    @Test
    fun getCellFromStringCoordinates() {
        val cell = grid[2][2]

        val actualCell = grid.getCellFromStringCoordinates("C3").getOrNull()!!

        assertEquals(cell, actualCell)
    }
}