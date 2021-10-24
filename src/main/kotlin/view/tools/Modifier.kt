package fr.tb_lab.view.tools

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
inline fun Modifier.applyIf(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) this.modifier() else this