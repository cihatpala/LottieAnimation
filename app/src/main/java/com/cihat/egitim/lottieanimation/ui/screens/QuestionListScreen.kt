package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.data.FolderHeading
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Finds heading index path for the provided names */
private fun findPath(names: List<String>, headings: List<FolderHeading>): List<Int> {
    val path = mutableListOf<Int>()
    var current = headings
    for (name in names) {
        val idx = current.indexOfFirst { it.name == name }
        if (idx == -1) break
        path.add(idx)
        current = current[idx].children
    }
    return path
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionListScreen(
    drawerState: DrawerState,
    questions: List<Question>,
    headings: List<FolderHeading>,
    onEdit: (Int, Question) -> Unit,
    onDelete: (Int) -> Unit,
    onBack: () -> Unit,
    drawerContent: @Composable (closeDrawer: () -> Unit) -> Unit = {}
) {
    AppScaffold(
        title = "Questions",
        drawerState = drawerState,
        drawerContent = drawerContent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(questions) { index, q ->
                    var showEdit by remember(q) { mutableStateOf(false) }
                    var newQuestion by remember(q) { mutableStateOf(q.text) }
                    var newAnswer by remember(q) { mutableStateOf(q.answer) }
                    var newTopic by remember(q) { mutableStateOf(q.topic) }
                    var newSub by remember(q) { mutableStateOf(q.subtopic) }
                    val initialPath = remember(q) {
                        val names = buildList {
                            if (q.topic.isNotBlank()) add(q.topic)
                            if (q.subtopic.isNotBlank()) {
                                addAll(q.subtopic.split(" > ").map { it.trim() }.filter { it.isNotBlank() })
                            }
                        }
                        findPath(names, headings)
                    }
                    var editPath by remember(q) { mutableStateOf(initialPath) }
                    val disableTopic = headings.size == 1 && q.topic == headings.firstOrNull()?.name

                    val swipeState = rememberSwipeableState(0)
                    val actionWidth = 72.dp
                    val maxOffset = with(LocalDensity.current) { (actionWidth * 2).toPx() }
                    val reveal = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)
                    val scope = rememberCoroutineScope()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                    showEdit = true
                                },
                                enabled = swipeState.currentValue == 1,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .height(72.dp)
                                    .padding(horizontal = 12.dp)
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
                                    onDelete(index)
                                },
                                enabled = swipeState.currentValue == 1,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.error)
                                    .height(72.dp)
                                    .padding(horizontal = 12.dp)
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
                            Text(text = q.topic)
                            Text(text = q.subtopic)
                            Text(text = q.text)
                            Text(text = q.answer)
                        }
                    }

                    if (showEdit) {
                        AlertDialog(
                            onDismissRequest = { showEdit = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val names = mutableListOf<String>()
                                    var list = headings
                                    for (idx in editPath) {
                                        val h = list.getOrNull(idx) ?: break
                                        names.add(h.name)
                                        list = h.children
                                    }
                                    val topic = if (headings.isEmpty()) newTopic else names.firstOrNull() ?: ""
                                    val sub = if (headings.isEmpty()) newSub else names.drop(1).joinToString(" > ")
                                    onEdit(index, Question(newQuestion, newAnswer, topic, sub))
                                    showEdit = false
                                }) { Text("Save") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showEdit = false }) { Text("Cancel") }
                            },
                            title = { Text("Edit Question") },
                            text = {
                                Column {
                                    if (headings.isEmpty()) {
                                        OutlinedTextField(
                                            value = newTopic,
                                            onValueChange = { newTopic = it },
                                            label = { Text("Topic") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = newSub,
                                            onValueChange = { newSub = it },
                                            label = { Text("Subtopic") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        var currentList = headings
                                        val startLevel = if (disableTopic) 1 else 0
                                        for (level in startLevel..editPath.size) {
                                            val options = currentList
                                            if (options.isEmpty()) break
                                            if (level > 0 || !disableTopic) {
                                                var expanded by remember(level, editPath) { mutableStateOf(false) }
                                                val selectedIdx = editPath.getOrNull(level)
                                                ExposedDropdownMenuBox(
                                                    expanded = expanded,
                                                    onExpandedChange = { expanded = !expanded }
                                                ) {
                                                    OutlinedTextField(
                                                        value = selectedIdx?.let { idx ->
                                                            if (idx in options.indices) options[idx].name else ""
                                                        } ?: "Seçiniz",
                                                        onValueChange = {},
                                                        readOnly = true,
                                                        label = { Text("Başlık ${level + 1}") },
                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                                        modifier = Modifier
                                                            .menuAnchor()
                                                            .fillMaxWidth()
                                                    )
                                                    ExposedDropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        options.forEachIndexed { i, h ->
                                                            DropdownMenuItem(
                                                                text = { Text(h.name) },
                                                                onClick = {
                                                                    editPath = editPath.take(level) + i
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                            val idx = editPath.getOrNull(level)
                                            currentList = if (idx != null && idx in options.indices) options[idx].children else emptyList()
                                        }
                                    }
                                    OutlinedTextField(
                                        value = newQuestion,
                                        onValueChange = { newQuestion = it },
                                        label = { Text("Question") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = newAnswer,
                                        onValueChange = { newAnswer = it },
                                        label = { Text("Answer") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

