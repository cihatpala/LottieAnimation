package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cihat.egitim.lottieanimation.ui.theme.ThemeMode
import androidx.navigation.compose.rememberNavController
import com.cihat.egitim.lottieanimation.ui.navigation.AppNavHost
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModelFactory
import kotlinx.coroutines.launch
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val quizViewModel: QuizViewModel by viewModels {
        val repo = (application as LottieApplication).repository
        QuizViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = this@MainActivity
            val repository = (application as LottieApplication).repository
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            var showDialog by remember { mutableStateOf(false) }
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(Unit) {
                themeMode = repository.getTheme()
            }

            LottieAnimationTheme(themeMode = themeMode) {
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

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    quizViewModel = quizViewModel,
                    themeMode = themeMode,
                    onThemeChange = {
                        themeMode = it
                        coroutineScope.launch { repository.saveTheme(it) }
                    },
                    drawerState = drawerState
                )
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
