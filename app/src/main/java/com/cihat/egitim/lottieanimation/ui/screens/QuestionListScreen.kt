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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import kotlinx.coroutines.launch

@Composable
fun QuestionListScreen(
    questions: List<Question>,
    onEdit: (Int, Question) -> Unit,
    onDelete: (Int) -> Unit,
    onBack: () -> Unit
) {
    AppScaffold(
        title = "Questions",
        showBack = true,
        onBack = onBack
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
                                    .background(Color(0xFFFFA500))
                                    .height(72.dp)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                            }
                            IconButton(
                                onClick = {
                                    scope.launch { swipeState.animateTo(0) }
                                    onDelete(index)
                                },
                                enabled = swipeState.currentValue == 1,
                                modifier = Modifier
                                    .background(Color.Red)
                                    .height(72.dp)
                                    .padding(horizontal = 12.dp)
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
                                    onEdit(index, Question(newQuestion, newAnswer, newTopic, newSub))
                                    showEdit = false
                                }) { Text("Save") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showEdit = false }) { Text("Cancel") }
                            },
                            title = { Text("Edit Question") },
                            text = {
                                Column {
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

