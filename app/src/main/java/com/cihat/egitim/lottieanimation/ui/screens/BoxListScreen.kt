package com.cihat.egitim.lottieanimation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.cihat.egitim.lottieanimation.data.FolderHeading
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@Composable
fun BoxListScreen(
    quizName: String,
    folderName: String,
    boxes: List<List<*>>,
    headings: List<FolderHeading>,
    onQuiz: (Int) -> Unit,
    onAddQuestion: (String, String, String, String, Int) -> Unit,
    onView: (Int) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    AppScaffold(
        title = quizName,
        showBack = true,
        onBack = onBack,
        onMenu = { onTab(BottomTab.MENU) },
        bottomTab = BottomTab.HOME,
        onTabSelected = onTab
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(boxes) { index, box ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { onView(index) }
                            .padding(4.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Box ${index + 1}")
                            Text(text = "${box.size} soru")
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(onClick = { onQuiz(index) }) { Text("Quiz") }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showAddDialog = true }) { Text("Add Question") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout) { Text("Logout") }

            if (showAddDialog) {
                AddQuestionDialog(
                    boxCount = boxes.size,
                    headings = headings,
                    quizName = quizName,
                    folderName = folderName,
                    onAdd = { q, a, topic, sub, box ->
                        onAddQuestion(q, a, topic, sub, box)
                    },
                    onDismiss = { showAddDialog = false }
                )
            }
        }
    }
}
