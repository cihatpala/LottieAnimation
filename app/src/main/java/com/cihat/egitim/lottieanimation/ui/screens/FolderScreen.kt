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
import com.cihat.egitim.lottieanimation.data.UserQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun FolderScreen(
    quizzes: List<UserQuiz>,
    onBack: () -> Unit
) {
    AppScaffold(
        title = "Folders",
        showBack = true,
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (quizzes.isEmpty()) {
                Text("No quizzes")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(quizzes) { quiz ->
                        val path = (quiz.categories + quiz.name).joinToString(" / ")
                        Text(path, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
