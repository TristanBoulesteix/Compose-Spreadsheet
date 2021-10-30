package fr.tb_lab.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onCloseRequest: () -> Unit,
    width: Dp = 400.dp,
    height: Dp = 200.dp
) {
    val state = rememberDialogState(size = DpSize(width, height))

    Dialog(
        onCloseRequest = onCloseRequest,
        title = title,
        resizable = false,
        state = state,
        onPreviewKeyEvent = {
            if (it.key == Key.Escape || it.key == Key.Enter)
                onCloseRequest()
            false
        }) {
        this.window.isModal = true

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1f).padding(10.dp).padding(top = 5.dp)) {
                Text(text = message, modifier = Modifier.fillMaxWidth())
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = onCloseRequest) {
                    Text(text = "OK")
                }
            }
        }
    }
}