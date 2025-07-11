package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import kotlinx.coroutines.launch
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

/** Simple node representing a folder */
private data class FolderNode(
    val label: String,
    val level: Int,
    val quizIndices: MutableList<Int> = mutableListOf(),
    val children: MutableList<FolderNode> = mutableListOf()
)

private fun buildTree(quizzes: List<UserQuiz>): List<FolderNode> {
    val roots = mutableMapOf<String, FolderNode>()
    quizzes.forEachIndexed { index, quiz ->
        var current = roots.getOrPut(quiz.name) { FolderNode(quiz.name, 0) }
        current.quizIndices.add(index)
        quiz.categories.forEachIndexed { i, cat ->
            current = current.children.find { it.label == cat }
                ?: FolderNode(cat, i + 1).also { current.children.add(it) }
            current.quizIndices.add(index)
        }
    }
    return roots.values.toList()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FolderItem(
    node: FolderNode,
    onRename: (FolderNode, String) -> Unit,
    onDelete: (FolderNode) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val showRename = remember { mutableStateOf(false) }
    val showDelete = remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(node.label) }
    val scope = rememberCoroutineScope()
    val actionWidth = 72.dp
    val swipeState = rememberSwipeableState(0)
    val maxOffset = with(androidx.compose.ui.platform.LocalDensity.current) { (actionWidth * 2).toPx() }
    val revealProgress = (-swipeState.offset.value / maxOffset).coerceIn(0f, 1f)

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
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
                    .background(Color.Transparent)
                    .alpha(revealProgress),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        scope.launch { swipeState.animateTo(0) }
                        showRename.value = true
                    },
                    enabled = swipeState.currentValue == 1,
                    modifier = Modifier
                        .background(Color(0xFFFFA500))
                        .height(72.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                }
                IconButton(
                    onClick = {
                        scope.launch { swipeState.animateTo(0) }
                        showDelete.value = true
                    },
                    enabled = swipeState.currentValue == 1,
                    modifier = Modifier
                        .background(Color.Red)
                        .height(72.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = node.children.isNotEmpty()) { expanded.value = !expanded.value }
                    .padding(start = (node.level * 16).dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (node.children.isNotEmpty()) {
                    Icon(
                        imageVector = if (expanded.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                Text(
                    text = node.label,
                    modifier = Modifier.padding(start = 4.dp),
                    fontSize = 18.sp
                )
            }
        }

        if (expanded.value) {
            node.children.forEach { child ->
                FolderItem(child, onRename, onDelete)
            }
        }
    }

    if (showRename.value) {
        AlertDialog(
            onDismissRequest = { showRename.value = false },
            confirmButton = {
                TextButton(onClick = {
                    onRename(node, newName)
                    showRename.value = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRename.value = false }) { Text("Cancel") } },
            title = { Text("Edit") },
            text = {
                OutlinedTextField(value = newName, onValueChange = { newName = it })
            }
        )
    }

    if (showDelete.value) {
        AlertDialog(
            onDismissRequest = { showDelete.value = false },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(node)
                    showDelete.value = false
                }) { Text("Evet") }
            },
            dismissButton = { TextButton(onClick = { showDelete.value = false }) { Text("Hayır") } },
            text = { Text("Silmek istediğinize emin misiniz?") }
        )
    }
}

@Composable
fun FolderScreen(
    quizzes: List<UserQuiz>,
    onRenameQuiz: (Int, String) -> Unit,
    onDeleteQuiz: (Int) -> Unit,
    onRenameCategory: (Int, Int, String) -> Unit,
    onDeleteCategory: (Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val roots = buildTree(quizzes)
    AppScaffold(
        title = "Folders",
        showBack = true,
        onBack = onBack
    ) {
        if (roots.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) { Text("No quizzes") }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                roots.forEach { node ->
                    item {
                        FolderItem(
                            node = node,
                            onRename = { n, label ->
                                n.quizIndices.toSet().forEach { idx ->
                                    if (n.level == 0) onRenameQuiz(idx, label)
                                    else onRenameCategory(idx, n.level - 1, label)
                                }
                            },
                            onDelete = { n ->
                                n.quizIndices.toSet().sortedDescending().forEach { idx ->
                                    if (n.level == 0) onDeleteQuiz(idx)
                                    else onDeleteCategory(idx, n.level - 1)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
