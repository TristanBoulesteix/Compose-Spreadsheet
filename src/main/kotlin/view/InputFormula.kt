package fr.tb_lab.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
fun InputFormula(
    contentText: String,
    focusRequester: FocusRequester,
    setContentText: (String) -> Unit
) = Box(modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
        value = contentText,
        onValueChange = setContentText,
        modifier = Modifier.padding(10.dp).fillMaxWidth().focusRequester(focusRequester),
        singleLine = true,
        placeholder = { Text("Enter a formula") }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}