package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.cihat.egitim.lottieanimation.ui.navigation.AppNavHost
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val activity = this@MainActivity
            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }

            LottieAnimationTheme {
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        text = { Text("Uygulamadan çıkmak istiyor musunuz?") },
                        confirmButton = {
                            TextButton(onClick = { activity.finish() }) {
                                Text("Evet")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Hayır")
                            }
                        }
                    )
                }

                AppNavHost(navController, authViewModel, quizViewModel)
                BackHandler(enabled = !showDialog) {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        showDialog = true
                    }
                }
            }
        }
    }
}
