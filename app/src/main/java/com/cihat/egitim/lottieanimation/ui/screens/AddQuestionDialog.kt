package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.FolderHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionDialog(
    boxCount: Int,
    headings: List<FolderHeading>,
    quizName: String,
    folderName: String,
    onAdd: (String, String, String, String, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var selectedBox by remember { mutableStateOf(0) }
    var path by remember { mutableStateOf<List<Int>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (questionText.isNotBlank() && answerText.isNotBlank()) {
                    var list = headings
                    val names = mutableListOf<String>()
                    for (idx in path) {
                        val h = list.getOrNull(idx) ?: break
                        names.add(h.name)
                        list = h.children
                    }
                    val topic = names.firstOrNull() ?: ""
                    val sub = names.drop(1).joinToString(" > ")
                    onAdd(questionText, answerText, topic, sub, selectedBox)
                    questionText = ""
                    answerText = ""
                    selectedBox = 0
                }
            }) { Text("Ekle") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bitti") }
        },
        title = {
            Text(
                text = "Soru Ekle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = quizName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    Spacer(modifier = Modifier.height(8.dp))

                    var currentList = headings
                for (level in 0..path.size) {
                    val options = currentList
                    if (options.isEmpty()) break
                    var expanded by remember(level, path) { mutableStateOf(false) }
                    val selectedIdx = path.getOrNull(level)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedIdx?.let { options[it].name } ?: "Seçiniz",
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
                            options.forEachIndexed { index, h ->
                                DropdownMenuItem(
                                    text = { Text(h.name) },
                                    onClick = {
                                        path = path.take(level) + index
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedIdx?.let { idx -> currentList = options[idx].children }
                        ?: run { currentList = emptyList() }
                }

                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Soru") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        label = { Text("Cevap") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

