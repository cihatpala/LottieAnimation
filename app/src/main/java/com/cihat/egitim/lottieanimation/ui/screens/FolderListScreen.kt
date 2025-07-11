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
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.cihat.egitim.lottieanimation.data.UserFolder
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FolderListScreen(
    folders: List<UserFolder>,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onCreate: (String, List<String>) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Klasörlerim",
        showBack = true,
        onBack = onBack,
        bottomTab = BottomTab.HOME,
        onTabSelected = onTab
    ) {
        var showCreate by remember { mutableStateOf(false) }
        var createName by remember { mutableStateOf("") }
        val subHeadings = remember { mutableStateListOf<String>() }
        var newSub by remember { mutableStateOf("") }

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
                    itemsIndexed(items = folders, key = { _, f -> f.id }) { index, folder ->
                        var expanded by remember(folder.id) { mutableStateOf(false) }
                        var showRename by remember(folder.id) { mutableStateOf(false) }
                        var showDelete by remember(folder.id) { mutableStateOf(false) }
                        var newName by remember(folder.id) { mutableStateOf(folder.name) }
                        val scope = rememberCoroutineScope()
                        val actionWidth = 72.dp
                        val swipeState = rememberSwipeableState(0)
                        val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }
                        val revealProgress = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
                        LaunchedEffect(swipeState.offset) {
                            if (swipeState.offset.value != 0f) expanded = false
                        }
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
                                    .alpha(revealProgress)
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch { swipeState.animateTo(0) }
                                        showRename = true
                                    },
                                    enabled = swipeState.currentValue == 1,
                                    modifier = Modifier
                                        .background(Color(0xFFFFA500))
                                        .size(actionWidth)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                }
                                IconButton(
                                    onClick = {
                                        scope.launch { swipeState.animateTo(0) }
                                        showDelete = true
                                    },
                                    enabled = swipeState.currentValue == 1,
                                    modifier = Modifier
                                        .background(Color.Red)
                                        .size(actionWidth)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
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
                                    Text(text = folder.name, modifier = Modifier.weight(1f))
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
                                        folder.subHeadings.forEach { sub ->
                                            Text(text = sub)
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
                                        onRename(index, newName)
                                        showRename = false
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showRename = false }) { Text("Cancel") }
                                },
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
                                        onDelete(index)
                                        showDelete = false
                                    }) { Text("Evet") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDelete = false }) { Text("Hayır") }
                                },
                                text = { Text("Silmek istediğinize emin misiniz?") }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Button(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Logout")
                }
            }
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Klasör Oluştur") },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                confirmButton = {
                    TextButton(onClick = {
                        onCreate(createName, subHeadings.toList())
                        showCreate = false
                        createName = ""
                        subHeadings.clear()
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
                        subHeadings.forEachIndexed { index, text ->
                            OutlinedTextField(
                                value = text,
                                onValueChange = { subHeadings[index] = it },
                                label = { Text("Alt Başlık ${index + 1}") },
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
                                value = newSub,
                                onValueChange = { newSub = it },
                                label = { Text("Alt Başlık Ekle") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                if (newSub.isNotBlank()) {
                                    subHeadings.add(newSub)
                                    newSub = ""
                                }
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add sub")
                            }
                        }
                    }
                }
            )
        }
    }
}
