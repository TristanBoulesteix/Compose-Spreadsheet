package fr.tb_lab.view.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import fr.tb_lab.model.AlphabeticalHeaderList
import fr.tb_lab.model.Cell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyScrollableGrid(
    modifier: Modifier = Modifier,
    grid: List<List<Cell>>,
    cellSize: DpSize,
    content: @Composable (Cell) -> Unit
) = Box(modifier) {
    val horizontalState = rememberScrollState()

    val verticalState = rememberLazyListState()

    Row(Modifier.padding(bottom = 12.dp)) {
        val headerRow = remember { List(grid.size) { (it + 1).toString() } }

        val headerRowState = rememberLazyListState()

        LazyColumn(state = headerRowState) {
            stickyHeader { HeaderCell("", cellSize) }

            items(headerRow) {
                HeaderCell(it, cellSize)
            }
        }

        LaunchedEffect(verticalState.firstVisibleItemScrollOffset) {
            headerRowState.scrollToItem(verticalState.firstVisibleItemIndex, verticalState.firstVisibleItemScrollOffset)
        }

        LaunchedEffect(headerRowState.firstVisibleItemScrollOffset) {
            verticalState.scrollToItem(headerRowState.firstVisibleItemIndex, headerRowState.firstVisibleItemScrollOffset)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(end = 12.dp)
                    .horizontalScroll(horizontalState),
                state = verticalState
            ) {
                stickyHeader {
                    val alphabeticalHeader = remember { AlphabeticalHeaderList(grid.size) }

                    Row {
                        alphabeticalHeader.forEach {
                            HeaderCell(it, cellSize)
                        }
                    }
                }

                items(grid, key = { it.first().x }) { row ->
                    Row {
                        row.forEach { content(it) }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = verticalState)
            )
        }
    }

    HorizontalScrollbar(
        modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().padding(end = 12.dp),
        adapter = rememberScrollbarAdapter(horizontalState)
    )
}

@Composable
private fun HeaderCell(value: String, size: DpSize) = Text(
    text = value,
    modifier = Modifier.size(size).border(width = 1.dp, color = Color.Black)
        .background(Color.White),
    textAlign = TextAlign.Center
)