package com.cihat.egitim.lottieanimation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.theme.ThemeMode
import com.cihat.egitim.lottieanimation.ui.screens.AuthScreen
import com.cihat.egitim.lottieanimation.ui.screens.LoginScreen
import com.cihat.egitim.lottieanimation.ui.screens.BoxListScreen
import com.cihat.egitim.lottieanimation.ui.screens.HomeFeedScreen
import com.cihat.egitim.lottieanimation.ui.screens.FolderListScreen
import com.cihat.egitim.lottieanimation.ui.screens.ProfileScreen
import com.cihat.egitim.lottieanimation.ui.screens.UserProfileScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuestionListScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuizListScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuizScreen
import com.cihat.egitim.lottieanimation.ui.screens.SettingsScreen
import com.cihat.egitim.lottieanimation.ui.screens.SplashScreen
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Auth : Screen("auth")
    data object Login : Screen("login")
    data object Settings : Screen("settings")
    data object QuizList : Screen("quizList")
    data object FolderList : Screen("folderList")
    data object Profile : Screen("profile")
    data object MyProfile : Screen("myProfile")
    data object BoxList : Screen("boxList")
    data object HomeFeed : Screen("homeFeed")
    data object Quiz : Screen("quiz")
    data object QuestionList : Screen("questionList/{boxIndex}") {
        const val boxArg = "boxIndex"
        fun createRoute(boxIndex: Int) = "questionList/$boxIndex"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    quizViewModel: QuizViewModel,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    openDrawer: () -> Unit,
    closeDrawer: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(BottomTab.HOME) }
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                delay(2500)
                navController.navigate(Screen.QuizList.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            SplashScreen()
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onGoogle = {
                    navController.navigate(Screen.QuizList.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogin = { navController.navigate(Screen.Login.route) },
                bottomTab = currentTab,
                onMenu = openDrawer,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, pass, result ->
                    authViewModel.login(email, pass) { success ->
                        result(success)
                        if (success) {
                            navController.navigate(Screen.QuizList.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            android.widget.Toast.makeText(
                                navController.context,
                                "Giriş başarısız",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onForgot = {},
                onSignup = { navController.navigate(Screen.Auth.route) },
                bottomTab = currentTab,
                onMenu = openDrawer,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                bottomTab = currentTab,
                onMenu = openDrawer,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        composable(Screen.MyProfile.route) {
            UserProfileScreen(
                user = authViewModel.currentUser,
                onBack = { navController.popBackStack() },
                bottomTab = currentTab,
                onMenu = openDrawer,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onBack = { navController.popBackStack() },
                bottomTab = currentTab,
                onMenu = openDrawer,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }
        composable(Screen.FolderList.route) {
                FolderListScreen(
                    folders = quizViewModel.folders,
                    onRename = { index, name -> quizViewModel.renameFolder(index, name) },
                    onDelete = { index -> quizViewModel.deleteFolder(index) },
                    onRenameHeading = { f, path, n -> quizViewModel.renameHeading(f, path, n) },
                    onDeleteHeading = { f, path -> quizViewModel.deleteHeading(f, path) },
                    onAddHeading = { f, path, n -> quizViewModel.addHeading(f, path, n) },
                    onCreate = { name, subs -> quizViewModel.createFolder(name, subs) },
                    onBack = { navController.popBackStack() },
                    bottomTab = currentTab,
                    onTab = { tab ->
                        closeDrawer()
                        currentTab = tab
                        when (tab) {
                            BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                            BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                            BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                        }
                    },
                    onMenu = openDrawer
            )
        }
        composable(Screen.QuizList.route) {
            QuizListScreen(
                quizzes = quizViewModel.quizzes,
                folders = quizViewModel.folders,
                onQuiz = { quizIdx, boxIdx ->
                    quizViewModel.setCurrentQuiz(quizIdx)
                    if (quizViewModel.startQuiz(boxIdx)) {
                        navController.navigate(Screen.Quiz.route)
                    } else {
                        android.widget.Toast.makeText(
                            navController.context,
                            "Bu kutuda soru yok",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onView = { quizIdx, boxIdx ->
                    quizViewModel.setCurrentQuiz(quizIdx)
                    navController.navigate(Screen.QuestionList.createRoute(boxIdx))
                },
                onRename = { index, name -> quizViewModel.renameQuiz(index, name) },
                onDelete = { index -> quizViewModel.deleteQuiz(index) },
                onMoveQuiz = { from, to -> quizViewModel.moveQuiz(from, to) },
                onCreate = { name, count, folderId ->
                    quizViewModel.createQuiz(name, count, emptyList(), folderId)
                },
                onCreateWithQuestion = { name, count, folderId, topic, sub, q, a ->
                    quizViewModel.createQuizWithQuestion(name, count, folderId, topic, sub, q, a)
                },
                onAddQuestion = { q, a, topic, sub, box ->
                    quizViewModel.addQuestion(q, a, topic, sub, box)
                },
                onSetCurrentQuiz = { idx -> quizViewModel.setCurrentQuiz(idx) },
                onFolders = { navController.navigate(Screen.FolderList.route) },
                onBack = { navController.popBackStack() },
                bottomTab = currentTab,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                },
                onMenu = openDrawer
            )
        }
        composable(Screen.BoxList.route) {
            BoxListScreen(
                quizName = quizViewModel.currentQuizName,
                folderName = quizViewModel.currentQuizFolderName,
                boxes = quizViewModel.boxes,
                headings = quizViewModel.currentQuizHeadingOptions,
                onQuiz = { index ->
                    if (quizViewModel.startQuiz(index)) {
                        navController.navigate(Screen.Quiz.route)
                    } else {
                        android.widget.Toast.makeText(
                            navController.context,
                            "Bu kutuda soru yok",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onAddQuestion = { q, a, topic, sub, box ->
                    quizViewModel.addQuestion(q, a, topic, sub, box)
                },
                onView = { index -> navController.navigate(Screen.QuestionList.createRoute(index)) },
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout(navController.context)
                    navController.navigate(Screen.QuizList.route) {
                        popUpTo(Screen.BoxList.route) { inclusive = true }
                    }
                },
                bottomTab = currentTab,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                },
                onMenu = openDrawer
            )
        }
        composable(Screen.HomeFeed.route) {
            val canPop = navController.previousBackStackEntry != null
            HomeFeedScreen(
                quizzes = quizViewModel.publicQuizzes,
                onImport = { index ->
                    quizViewModel.importQuiz(quizViewModel.publicQuizzes[index])
                    android.widget.Toast.makeText(
                        navController.context,
                        "Quiz imported",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(Screen.QuizList.route)
                },
                showBack = canPop,
                onBack = { navController.popBackStack() },
                bottomTab = currentTab,
                onTab = { tab ->
                    closeDrawer()
                    currentTab = tab
                    when (tab) {
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> {}
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                    }
                },
                onMenu = openDrawer
            )
        }
        composable(Screen.Quiz.route) {
            QuizScreen(
                question = quizViewModel.currentQuestion,
                isAnswerVisible = quizViewModel.isAnswerVisible,
                onReveal = { quizViewModel.revealAnswer() },
                onAnswer = { correct ->
                    val more = quizViewModel.onAnswerSelected(correct)
                    if (!more) navController.popBackStack()
                },
                onQuit = { navController.popBackStack() },
                onMenu = openDrawer
            )
        }
        composable(
            route = Screen.QuestionList.route,
            arguments = listOf(navArgument(Screen.QuestionList.boxArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt(Screen.QuestionList.boxArg) ?: 0
            QuestionListScreen(
                questions = quizViewModel.boxes.getOrNull(index).orEmpty(),
                headings = quizViewModel.currentQuizHeadingOptions,
                onEdit = { qIdx, q -> quizViewModel.editQuestion(index, qIdx, q) },
                onDelete = { qIdx -> quizViewModel.deleteQuestion(index, qIdx) },
                onBack = { navController.popBackStack() },
                onMenu = openDrawer
            )
        }
    }
}
