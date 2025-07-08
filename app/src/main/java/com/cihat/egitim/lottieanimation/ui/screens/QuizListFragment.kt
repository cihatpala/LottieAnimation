package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel

class QuizListFragment : Fragment() {
    private val viewModel: QuizViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    QuizListScreen(
                        quizzes = viewModel.quizzes,
                        onQuiz = { quizIdx, boxIdx ->
                            viewModel.setCurrentQuiz(quizIdx)
                            if (viewModel.startQuiz(boxIdx)) {
                                findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.quizFragment)
                            } else {
                                android.widget.Toast.makeText(requireContext(), "Bu kutuda soru yok", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        onView = { quizIdx, boxIdx ->
                            viewModel.setCurrentQuiz(quizIdx)
                            findNavController().navigate(
                                com.cihat.egitim.lottieanimation.R.id.questionListFragment,
                                Bundle().apply { putInt("boxIndex", boxIdx) }
                            )
                        },
                        onAdd = { quizIdx ->
                            viewModel.setCurrentQuiz(quizIdx)
                            findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.addQuestionFragment)
                        },
                        onLogout = {
                            authViewModel.logout()
                            findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.authFragment)
                        },
                        onBack = { findNavController().navigateUp() },
                        onTab = { tab ->
                            when (tab) {
                                BottomTab.PROFILE -> {}
                                BottomTab.EXPLORE -> findNavController().navigate(R.id.homeFeedFragment)
                                BottomTab.HOME -> TODO()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizListScreen(
    quizzes: List<com.cihat.egitim.lottieanimation.data.UserQuiz>,
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onAdd: (Int) -> Unit,
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
                androidx.compose.material3.Text("Henüz quiziniz yok")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(quizzes) { quizIndex, quiz ->
                        var expanded by remember { mutableStateOf(false) }
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
                }
                Button(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Logout")
                }
            }
        }
    }
}
