package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel

class AddQuestionFragment : Fragment() {
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    AddQuestionScreen(viewModel.boxes.size,
                        onAdd = { q, a, box ->
                            viewModel.addQuestion(q, a, box)
                        },
                        onDone = {
                            findNavController().navigateUp()
                        })
                }
            }
        }
    }
}

@Composable
private fun AddQuestionScreen(
    boxCount: Int,
    onAdd: (String, String, Int) -> Unit,
    onDone: () -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var selectedBox by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until boxCount) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { selectedBox = i },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(2.dp)
                            .let {
                                if (selectedBox == i) {
                                    it.border(BorderStroke(2.dp, Color.Blue), RoundedCornerShape(4.dp))
                                } else {
                                    it.border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
                                }
                            }
                    ) {}
                    Text(text = "${i + 1}")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                onAdd(questionText, answerText, selectedBox)
                questionText = ""
                answerText = ""
                selectedBox = 0
            }) {
                Text("Add")
            }
            Button(onClick = onDone) { Text("Done") }
        }
    }
}
