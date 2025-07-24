package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
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
            val drawerState = rememberDrawerState(DrawerValue.Closed)
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

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            AppDrawer(
                                isLoggedIn = authViewModel.currentUser != null,
                                onClose = { coroutineScope.launch { drawerState.close() } },
                                onProfileInfo = { navController.navigate(Screen.MyProfile.route) },
                                onPro = {},
                                onAuth = { navController.navigate(Screen.Auth.route) },
                                onSettings = { navController.navigate(Screen.Settings.route) },
                                onFolders = { navController.navigate(Screen.FolderList.route) },
                                onSupport = {},
                                onRate = {},
                                onLogout = {
                                    authViewModel.logout(navController.context)
                                    coroutineScope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        quizViewModel = quizViewModel,
                        themeMode = themeMode,
                        onThemeChange = {
                            themeMode = it
                            coroutineScope.launch { repository.saveTheme(it) }
                        },
                        openDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
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
