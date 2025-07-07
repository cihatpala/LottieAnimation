package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
                        onTab = { tab ->
                            when (tab) {
                                BottomTab.PROFILE -> {}
                                BottomTab.EXPLORE -> findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.homeFeedFragment)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setMessage("Uygulamadan çıkmak istiyor musunuz?")
                .setPositiveButton("Evet") { _, _ -> requireActivity().finish() }
                .setNegativeButton("Hayır", null)
                .show()
        }
    }
}

@Composable
private fun QuizListScreen(
    quizzes: List<com.cihat.egitim.lottieanimation.data.UserQuiz>,
    onQuiz: (Int, Int) -> Unit,
    onView: (Int, Int) -> Unit,
    onAdd: (Int) -> Unit,
    onLogout: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "My Quizzes",
        showBack = false,
        onBack = {},
        bottomTab = BottomTab.PROFILE,
        onTabSelected = onTab
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(quiz.boxes) { boxIndex, box ->
                                    Box(
                                        modifier = Modifier
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
