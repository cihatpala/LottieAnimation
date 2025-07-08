package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel

class HomeFeedFragment : Fragment() {
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    val canPop = findNavController().previousBackStackEntry != null
                    HomeFeedScreen(
                        quizzes = viewModel.publicQuizzes,
                        onImport = { index ->
                            viewModel.importQuiz(viewModel.publicQuizzes[index])
                            android.widget.Toast.makeText(requireContext(), "Quiz imported", android.widget.Toast.LENGTH_SHORT).show()
                            findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.quizListFragment)
                        },
                        showBack = canPop,
                        onBack = { findNavController().navigateUp() },
                        onTab = { tab ->
                            when (tab) {
                                BottomTab.PROFILE -> findNavController().navigate(R.id.quizListFragment)
                                BottomTab.EXPLORE -> {}
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
fun HomeFeedScreen(
    quizzes: List<com.cihat.egitim.lottieanimation.data.PublicQuiz>,
    onImport: (Int) -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Explore",
        showBack = showBack,
        onBack = onBack,
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
                            .fillMaxWidth()
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
