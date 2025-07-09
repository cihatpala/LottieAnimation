package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun QuestionListScreen(
    questions: List<Question>,
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
                items(questions) { q ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = q.text)
                        Text(text = q.answer)
                    }
                }
            }
        }
    }
}
