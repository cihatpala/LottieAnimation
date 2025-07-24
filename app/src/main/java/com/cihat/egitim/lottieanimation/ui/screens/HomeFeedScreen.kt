package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.data.PublicQuiz
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@Composable
fun HomeFeedScreen(
    quizzes: List<PublicQuiz>,
    onImport: (Int) -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Explore",
        showBack = showBack,
        onBack = onBack,
        onMenu = { onTab(BottomTab.MENU) },
        bottomTab = BottomTab.EXPLORE,
        onTabSelected = onTab
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(quizzes) { index, quiz ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "${quiz.name} - by ${quiz.author}")
                        Text(text = "${quiz.questions.size} questions")
                        Button(onClick = { onImport(index) }) { Text("Import") }
                    }
                }
            }
        }
    }
}
