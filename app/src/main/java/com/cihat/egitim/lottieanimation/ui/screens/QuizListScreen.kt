package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuizListScreen(
    quizzes: List<UserQuiz>,
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onAdd: (Int) -> Unit,
    onRename: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "My Quizzes",
        showBack = true,
        onBack = onBack,
        bottomTab = BottomTab.HOME,
        onTabSelected = onTab
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quizzes.isEmpty()) {
                Text("Henüz quiziniz yok")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(quizzes) { quizIndex, quiz ->
                        var expanded by remember { mutableStateOf(false) }
                        var showRename by remember { mutableStateOf(false) }
                        var showDelete by remember { mutableStateOf(false) }
                        var newName by remember { mutableStateOf(quiz.name) }
                        val dismissState = rememberDismissState(positionalThreshold = { 300.dp })

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { showRename = true }) { Text("Edit") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(onClick = { showDelete = true }) { Text("Delete") }
                                }
                            },
                            dismissContent = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expanded = !expanded }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = quiz.name, modifier = Modifier.weight(1f))
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }
                                    if (expanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            quiz.boxes.chunked(2).forEachIndexed { rowIndex, pair ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    pair.forEachIndexed { colIndex, box ->
                                                        val boxIndex = rowIndex * 2 + colIndex
                                                        Box(
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .aspectRatio(1f)
                                                                .clickable { onView(quizIndex, boxIndex) }
                                                                .padding(4.dp)
                                                                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text(text = "Box ${boxIndex + 1}")
                                                                Text(text = "${box.size} soru")
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Button(onClick = { onQuiz(quizIndex, boxIndex) }) { Text("Quiz") }
                                                            }
                                                        }
                                                    }
                                                    if (pair.size == 1) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                        }
                                        Button(
                                            onClick = { onAdd(quizIndex) },
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .align(Alignment.CenterHorizontally)
                                        ) { Text("Add Question") }
                                    }
                                }
                            }
                        )

                        if (showRename) {
                            AlertDialog(
                                onDismissRequest = { showRename = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        onRename(quizIndex, newName)
                                        showRename = false
                                    }) { Text("Save") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showRename = false }) { Text("Cancel") }
                                },
                                title = { Text("Edit Quiz") },
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
                                        onDelete(quizIndex)
                                        showDelete = false
                                    }) { Text("Evet") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDelete = false }) { Text("Hayır") }
                                },
                                text = { Text("Silmek istediğinize emin misiniz?") }
                            )
                        }
                    }
                }
                Button(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Logout")
                }
            }
        }
    }
}
