package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.cihat.egitim.lottieanimation.ui.theme.ThemeMode
import androidx.navigation.compose.rememberNavController
import com.cihat.egitim.lottieanimation.ui.navigation.AppNavHost
import com.cihat.egitim.lottieanimation.ui.navigation.Screen
import com.cihat.egitim.lottieanimation.ui.components.AppDrawer
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModelFactory
import kotlinx.coroutines.launch
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme

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
            var showDialog by remember { mutableStateOf(false) }
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
           val coroutineScope = rememberCoroutineScope()
            var isDrawerOpen by remember { mutableStateOf(false) }
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

                Box {
                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        quizViewModel = quizViewModel,
                        themeMode = themeMode,
                        onThemeChange = {
                            themeMode = it
                            coroutineScope.launch { repository.saveTheme(it) }
                        },
                        openDrawer = { isDrawerOpen = true }
                    )

                    val drawerWidth = 300.dp
                    val offsetX by animateDpAsState(
                        targetValue = if (isDrawerOpen) 0.dp else -drawerWidth,
                        label = "drawer"
                    )
                    Box(
                        modifier = Modifier
                            .offset { androidx.compose.ui.unit.IntOffset(offsetX.roundToPx(), 0) }
                            .fillMaxHeight()
                            .width(drawerWidth)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(bottom = 80.dp)
                    ) {
                        val closeDrawer: () -> Unit = { isDrawerOpen = false }
                        AppDrawer(
                            isLoggedIn = authViewModel.currentUser != null,
                            onClose = closeDrawer,
                            onProfileInfo = {
                                navController.navigate(Screen.MyProfile.route)
                                closeDrawer()
                            },
                            onPro = {},
                            onAuth = {
                                navController.navigate(Screen.Auth.route)
                                closeDrawer()
                            },
                            onSettings = {
                                navController.navigate(Screen.Settings.route)
                                closeDrawer()
                            },
                            onFolders = {
                                navController.navigate(Screen.FolderList.route)
                                closeDrawer()
                            },
                            onSupport = {},
                            onRate = {},
                            onLogout = {
                                authViewModel.logout(navController.context)
                                closeDrawer()
                            }
                        )
                    }
                }
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
