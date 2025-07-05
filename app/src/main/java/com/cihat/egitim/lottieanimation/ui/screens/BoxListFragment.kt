package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

class BoxListFragment : Fragment() {
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    BoxListScreen(
                        boxes = viewModel.boxes,
                        onQuiz = { index ->
                            viewModel.startQuiz(index)
                            findNavController().navigate(
                                com.cihat.egitim.lottieanimation.R.id.quizFragment
                            )
                        },
                        onAdd = {
                            findNavController().navigate(
                                com.cihat.egitim.lottieanimation.R.id.addQuestionFragment
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxListScreen(
    boxes: List<List<*>>,
    onQuiz: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        boxes.forEachIndexed { index, box ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Box ${index + 1}: ${box.size} questions")
                Button(onClick = { onQuiz(index) }) { Text("Quiz") }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAdd) { Text("Add Question") }
    }
}
