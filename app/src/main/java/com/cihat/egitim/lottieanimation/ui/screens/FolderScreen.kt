package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

/** Simple node representing a folder */
private data class FolderNode(
    val label: String,
    val children: MutableList<FolderNode> = mutableListOf()
)

private fun buildTree(quizzes: List<UserQuiz>): List<FolderNode> {
    val roots = mutableMapOf<String, FolderNode>()
    quizzes.forEach { quiz ->
        var current = roots.getOrPut(quiz.name) { FolderNode(quiz.name) }
        quiz.categories.forEach { cat ->
            current = current.children.find { it.label == cat }
                ?: FolderNode(cat).also { current.children.add(it) }
        }
    }
    return roots.values.toList()
}

@Composable
private fun FolderItem(node: FolderNode, level: Int = 0) {
    val expanded = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = (level * 16).dp, top = 4.dp, bottom = 4.dp)
            .clickable(enabled = node.children.isNotEmpty()) { expanded.value = !expanded.value },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (node.children.isNotEmpty()) {
            Icon(
                imageVector = if (expanded.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        Text(node.label, modifier = Modifier.padding(start = 4.dp))
    }
    if (expanded.value) {
        node.children.forEach { child -> FolderItem(child, level + 1) }
    }
}

@Composable
fun FolderScreen(
    quizzes: List<UserQuiz>,
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
                    item { FolderItem(node) }
                }
            }
        }
    }
}
