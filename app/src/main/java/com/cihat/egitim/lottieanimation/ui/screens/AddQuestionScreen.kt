package com.cihat.egitim.lottieanimation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun AddQuestionScreen(
    boxCount: Int,
    onAdd: (String, String, String, String, Int) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var topicText by remember { mutableStateOf("") }
    var subTopicText by remember { mutableStateOf("") }
    var selectedBox by remember { mutableStateOf(0) }
    val context = LocalContext.current

    AppScaffold(
        title = "Add Question",
        showBack = true,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = topicText,
                onValueChange = { topicText = it },
                label = { Text("Topic") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = subTopicText,
                onValueChange = { subTopicText = it },
                label = { Text("Subtopic") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = answerText,
                onValueChange = { answerText = it },
                label = { Text("Answer") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until boxCount) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { selectedBox = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(2.dp)
                                .let {
                                    if (selectedBox == i) {
                                        it.border(BorderStroke(2.dp, Color.Blue), RoundedCornerShape(4.dp))
                                    } else {
                                        it.border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                                    }
                                }
                        ) {}
                        Text(text = "${i + 1}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    if (questionText.isBlank() || answerText.isBlank()) {
                        Toast.makeText(context, "Question and answer cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        onAdd(questionText, answerText, topicText, subTopicText, selectedBox)
                        questionText = ""
                        answerText = ""
                        topicText = ""
                        subTopicText = ""
                        selectedBox = 0
                    }
                }) {
                    Text("Add")
                }
                Button(onClick = onDone) { Text("Done") }
            }
        }
    }
}
