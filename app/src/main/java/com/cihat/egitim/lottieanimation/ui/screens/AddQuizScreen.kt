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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    existingNames: List<String>,
    onCreate: (String, Int, List<String>) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var boxValue by remember { mutableFloatStateOf(4f) }
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
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    existingNames.forEach { existing ->
                        DropdownMenuItem(
                            text = { Text(existing) },
                            onClick = {
                                name = existing
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Slider(
                    value = boxValue,
                    onValueChange = { boxValue = it },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = "${boxValue.toInt()} kutu")
            }
            categories.forEachIndexed { index, text ->
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { categories[index] = it },
                    label = { Text("Category ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (categories.size < 2) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { categories.add("") }) { Text("Add Category") }
            }
            val path = listOf(name) + categories
            if (path.any { it.isNotBlank() }) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Path: " + path.filter { it.isNotBlank() }.joinToString(" / "))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val count = boxValue.toInt()
                if (name.isNotBlank() && count > 0) {
                    onCreate(name, count, categories.filter { it.isNotBlank() })
                }
            }) {
                Text("Create")
            }
        }
    }
}

