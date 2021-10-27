package fr.tb_lab.view.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import fr.tb_lab.model.Cell
import fr.tb_lab.model.Grid

@Composable
fun LazyScrollableGrid(
    modifier: Modifier = Modifier,
    grid: Grid,
    cellSize: DpSize,
    content: @Composable (cell: Cell, rowIndex: Int, columnIndex: Int) -> Unit
) = Box(modifier) {
    val horizontalState = rememberScrollState()

    val verticalState = rememberLazyListState()

    Row(Modifier.padding(bottom = 12.dp)) {
        val headerRow = remember { List(grid.size) { (it + 1).toString() } }

        val headerRowState = rememberLazyListState()

        RowHeader(headerRowState, cellSize, headerRow)

        LaunchedEffect(verticalState.firstVisibleItemScrollOffset) {
            headerRowState.scrollToItem(verticalState.firstVisibleItemIndex, verticalState.firstVisibleItemScrollOffset)
        }

        LaunchedEffect(headerRowState.firstVisibleItemScrollOffset) {
            verticalState.scrollToItem(
                headerRowState.firstVisibleItemIndex,
                headerRowState.firstVisibleItemScrollOffset
            )
        }

        ContentGrid(horizontalState, verticalState, grid, cellSize, content)
    }

    HorizontalScrollbar(
        modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().padding(end = 12.dp),
        adapter = rememberScrollbarAdapter(horizontalState)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RowHeader(
    headerRowState: LazyListState,
    cellSize: DpSize,
    headerRow: List<String>
) = LazyColumn(state = headerRowState) {
    stickyHeader { HeaderCell("", cellSize) }

    items(headerRow) {
        HeaderCell(it, cellSize)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentGrid(
    horizontalState: ScrollState,
    verticalState: LazyListState,
    grid: Grid,
    cellSize: DpSize,
    content: @Composable (Cell, Int, Int) -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(end = 12.dp)
            .horizontalScroll(horizontalState),
        state = verticalState
    ) {
        stickyHeader {
            Row {
                grid.alphaHeader.forEach {
                    HeaderCell(it, cellSize)
                }
            }
        }

        grid.forEachIndexed { rowIndex, cells ->
            item(rowIndex) {
                Row {
                    cells.forEachIndexed { columnIndex, cell -> content(cell, rowIndex, columnIndex) }
                }
            }
        }
    }

    VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState = verticalState)
    )
}

@Composable
private fun HeaderCell(value: String, size: DpSize) = Text(
    text = value,
    modifier = Modifier.size(size).border(width = 1.dp, color = Color.Black)
        .background(Color.White),
    textAlign = TextAlign.Center
)