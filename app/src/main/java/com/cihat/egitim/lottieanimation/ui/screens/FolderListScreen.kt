package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.FolderHeading
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.components.OverflowMenu
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FolderListScreen(
    folders: List<UserFolder>,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onRenameHeading: (Int, List<Int>, String) -> Unit,
    onDeleteHeading: (Int, List<Int>) -> Unit,
    onAddHeading: (Int, List<Int>, String) -> Unit,
    onCreate: (String, List<String>) -> Unit,
    onBack: () -> Unit,
    bottomTab: BottomTab,
    onTab: (BottomTab) -> Unit,
    onMenu: () -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    AppScaffold(
        title = "Klasörlerim",
        showBack = true,
        onBack = onBack,
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab,
        actions = {
            OverflowMenu(listOf(
                "Klasör Oluştur" to { showCreate = true }
            ))
        }
    ) {
        var createName by remember { mutableStateOf("") }
        val newHeadings = remember { mutableStateListOf<String>() }
        var newHeadingText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (folders.isEmpty()) {
                Text("Henüz klasör yok")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(folders, key = { _, f -> f.id }) { index, folder ->
                        FolderItem(
                            folder = folder,
                            folderIndex = index,
                            onRename = onRename,
                            onDelete = onDelete,
                            onRenameHeading = onRenameHeading,
                            onDeleteHeading = onDeleteHeading,
                            onAddHeading = onAddHeading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                confirmButton = {
                    TextButton(onClick = {
                        onCreate(createName, newHeadings.toList())
                        showCreate = false
                        createName = ""
                        newHeadings.clear()
                        newHeadingText = ""
                    }) { Text("Oluştur") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreate = false }) { Text("İptal") }
                },
                title = { Text("Klasör Oluştur") },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedTextField(
                            value = createName,
                            onValueChange = { createName = it },
                            label = { Text("Ad") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        newHeadings.forEachIndexed { i, text ->
                            OutlinedTextField(
                                value = text,
                                onValueChange = { newHeadings[i] = it },
                                label = { Text("Başlık ${i + 1}") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = newHeadingText,
                                onValueChange = { newHeadingText = it },
                                label = { Text("Başlık Ekle") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (newHeadingText.isNotBlank()) {
                                    newHeadings.add(newHeadingText)
                                    newHeadingText = ""
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FolderItem(
    folder: UserFolder,
    folderIndex: Int,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onRenameHeading: (Int, List<Int>, String) -> Unit,
    onDeleteHeading: (Int, List<Int>) -> Unit,
    onAddHeading: (Int, List<Int>, String) -> Unit
) {
    var expanded by remember(folder.id) { mutableStateOf(false) }
    var showRename by remember(folder.id) { mutableStateOf(false) }
    var showDelete by remember(folder.id) { mutableStateOf(false) }
    var newName by remember(folder.id) { mutableStateOf(folder.name) }
    val scope = rememberCoroutineScope()
    val actionWidth = 72.dp
    val swipeState = rememberSwipeableState(0)
    val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }
    val reveal = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
    LaunchedEffect(swipeState.offset) { if (swipeState.offset.value != 0f) expanded = false }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .swipeable(
                state = swipeState,
                anchors = mapOf(0f to 0, -maxOffset to 1),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(72.dp)
                .alpha(reveal)
        ) {
            IconButton(
                onClick = {
                    scope.launch { swipeState.animateTo(0) }
                    showRename = true
                },
                enabled = swipeState.currentValue == 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .size(actionWidth)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            IconButton(
                onClick = {
                    scope.launch { swipeState.animateTo(0) }
                    showDelete = true
                },
                enabled = swipeState.currentValue == 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error)
                    .size(actionWidth)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }

        Column(
            modifier = Modifier
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(folder.name, modifier = Modifier.weight(1f))
                if (swipeState.offset.value == 0f) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, top = 4.dp)
                ) {
                    folder.headings.forEachIndexed { idx, heading ->
                        HeadingItem(
                            heading = heading,
                            folderIndex = folderIndex,
                            path = listOf(idx),
                            onRenameHeading = onRenameHeading,
                            onDeleteHeading = onDeleteHeading,
                            onAddHeading = onAddHeading
                        )
                    }
                    var newSub by remember(folder.id) { mutableStateOf("") }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = newSub,
                            onValueChange = { newSub = it },
                            label = { Text("Başlık Ekle") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            if (newSub.isNotBlank()) {
                                onAddHeading(folderIndex, emptyList(), newSub)
                                newSub = ""
                            }
                        }) { Icon(Icons.Default.Add, contentDescription = "Add") }
                    }
                }
            }
        }
    }

    if (showRename) {
        AlertDialog(
            onDismissRequest = { showRename = false },
            confirmButton = {
                TextButton(onClick = {
                    onRename(folderIndex, newName)
                    showRename = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRename = false }) { Text("Cancel") } },
            title = { Text("Edit Folder") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
            }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(folderIndex)
                    showDelete = false
                }) { Text("Evet") }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Hayır") } },
            text = { Text("Silmek istediğinize emin misiniz?") }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HeadingItem(
    heading: FolderHeading,
    folderIndex: Int,
    path: List<Int>,
    onRenameHeading: (Int, List<Int>, String) -> Unit,
    onDeleteHeading: (Int, List<Int>) -> Unit,
    onAddHeading: (Int, List<Int>, String) -> Unit
) {
    var expanded by remember(heading.id) { mutableStateOf(false) }
    var showRename by remember(heading.id) { mutableStateOf(false) }
    var showDelete by remember(heading.id) { mutableStateOf(false) }
    var newName by remember(heading.id) { mutableStateOf(heading.name) }
    val scope = rememberCoroutineScope()
    val actionWidth = 64.dp
    val swipeState = rememberSwipeableState(0)
    val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }
    val reveal = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
    LaunchedEffect(swipeState.offset) { if (swipeState.offset.value != 0f) expanded = false }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .swipeable(
                state = swipeState,
                anchors = mapOf(0f to 0, -maxOffset to 1),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(48.dp)
                .alpha(reveal)
        ) {
            IconButton(
                onClick = {
                    scope.launch { swipeState.animateTo(0) }
                    showRename = true
                },
                enabled = swipeState.currentValue == 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .size(actionWidth)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            IconButton(
                onClick = {
                    scope.launch { swipeState.animateTo(0) }
                    showDelete = true
                },
                enabled = swipeState.currentValue == 1,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error)
                    .size(actionWidth)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }

        Column(
            modifier = Modifier
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(heading.name, modifier = Modifier.weight(1f))
                if (swipeState.offset.value == 0f) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp)
                ) {
                    heading.children.forEachIndexed { idx, child ->
                        HeadingItem(
                            heading = child,
                            folderIndex = folderIndex,
                            path = path + idx,
                            onRenameHeading = onRenameHeading,
                            onDeleteHeading = onDeleteHeading,
                            onAddHeading = onAddHeading
                        )
                    }
                    var newSub by remember(heading.id) { mutableStateOf("") }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = newSub,
                            onValueChange = { newSub = it },
                            label = { Text("Alt Başlık") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            if (newSub.isNotBlank()) {
                                onAddHeading(folderIndex, path, newSub)
                                newSub = ""
                            }
                        }) { Icon(Icons.Default.Add, contentDescription = "Add") }
                    }
                }
            }
        }
    }

    if (showRename) {
        AlertDialog(
            onDismissRequest = { showRename = false },
            confirmButton = {
                TextButton(onClick = {
                    onRenameHeading(folderIndex, path, newName)
                    showRename = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRename = false }) { Text("Cancel") } },
            title = { Text("Edit Heading") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") }
                )
            }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteHeading(folderIndex, path)
                    showDelete = false
                }) { Text("Evet") }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Hayır") } },
            text = { Text("Silmek istediğinize emin misiniz?") }
        )
    }
}
