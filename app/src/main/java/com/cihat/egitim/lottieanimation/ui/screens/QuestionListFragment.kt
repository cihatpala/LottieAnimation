package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel

class QuestionListFragment : Fragment() {
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val boxIndex = requireArguments().getInt("boxIndex")
        val questions = viewModel.boxes.getOrNull(boxIndex).orEmpty()
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    QuestionListScreen(questions) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionListScreen(
    questions: List<com.cihat.egitim.lottieanimation.data.Question>,
    onBack: () -> Unit
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
        Button(onClick = onBack) { Text("Back") }
    }
}
