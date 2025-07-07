package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
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
                        quizzes = viewModel.quizzes.map { it.name },
                        onSelect = {
                            viewModel.setCurrentQuiz(it)
                            findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.boxListFragment)
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
    quizzes: List<String>,
    onSelect: (Int) -> Unit,
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
                itemsIndexed(quizzes) { index, name ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = name)
                        Button(onClick = { onSelect(index) }) { Text("View Boxes") }
                    }
                }
            }
            Button(onClick = onLogout, modifier = Modifier.padding(top = 8.dp)) { Text("Logout") }
        }
    }
}
