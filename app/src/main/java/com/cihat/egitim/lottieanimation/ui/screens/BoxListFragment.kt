package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
                        },
                        onView = { index ->
                            findNavController().navigate(
                                com.cihat.egitim.lottieanimation.R.id.questionListFragment,
                                Bundle().apply { putInt("boxIndex", index) }
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
    onAdd: () -> Unit,
    onView: (Int) -> Unit
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
        Button(onClick = onAdd) { Text("Add Question") }
    }
}
