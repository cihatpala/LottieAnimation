package com.cihat.egitim.lottieanimation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun OverflowMenu(items: List<Pair<String, () -> Unit>>) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    IconButton(onClick = { setExpanded(true) }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
        items.forEach { (title, action) ->
            DropdownMenuItem(text = { Text(title) }, onClick = {
                setExpanded(false)
                action()
            })
        }
    }
}
