package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun AddQuizScreen(
    onCreate: (String, Int, List<String>) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var boxText by remember { mutableStateOf("4") }
    val categories = remember { mutableStateListOf<String>() }

    AppScaffold(
        title = "Add Quiz",
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
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = boxText,
                onValueChange = { boxText = it },
                label = { Text("Box count") },
                modifier = Modifier.fillMaxWidth()
            )
            categories.forEachIndexed { index, text ->
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { categories[index] = it },
                    label = { Text("Category ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { categories.add("") }) { Text("Add Category") }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val count = boxText.toIntOrNull() ?: 0
                if (name.isNotBlank() && count > 0) {
                    onCreate(name, count, categories.filter { it.isNotBlank() })
                }
            }) {
                Text("Create")
            }
        }
    }
}
