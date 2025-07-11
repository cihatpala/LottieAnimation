package com.cihat.egitim.lottieanimation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.screens.AddQuestionScreen
import com.cihat.egitim.lottieanimation.ui.screens.AddQuizScreen
import com.cihat.egitim.lottieanimation.ui.screens.FolderScreen
import com.cihat.egitim.lottieanimation.ui.screens.AuthScreen
import com.cihat.egitim.lottieanimation.ui.screens.BoxListScreen
import com.cihat.egitim.lottieanimation.ui.screens.HomeFeedScreen
import com.cihat.egitim.lottieanimation.ui.screens.ProfileScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuestionListScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuizListScreen
import com.cihat.egitim.lottieanimation.ui.screens.QuizScreen
import com.cihat.egitim.lottieanimation.ui.screens.SettingsScreen
import com.cihat.egitim.lottieanimation.ui.screens.SplashScreen
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Auth : Screen("auth")
    data object Settings : Screen("settings")
    data object QuizList : Screen("quizList")
    data object Profile : Screen("profile")
    data object BoxList : Screen("boxList")
    data object Folders : Screen("folders")
    data object AddQuiz : Screen("addQuiz")
    data object AddQuestion : Screen("addQuestion")
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
    quizViewModel: QuizViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            LaunchedEffect(Unit) {
                delay(2500)
                if (authViewModel.currentUser != null) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
            SplashScreen()
        }
        composable(Screen.Auth.route) {
            AuthScreen(
                onGoogle = { token ->
                    authViewModel.loginWithGoogle(token) { success ->
                        if (success) {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            val canPop = navController.previousBackStackEntry != null
            ProfileScreen(
                onPro = {},
                onAuth = { navController.navigate(Screen.Auth.route) },
                onSettings = { navController.navigate(Screen.Settings.route) },
                onFolders = { navController.navigate(Screen.Folders.route) },
                onSupport = {},
                onRate = {},
                showBack = canPop,
                onBack = { navController.popBackStack() },
                onTab = { tab ->
                    when (tab) {
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onCreateQuiz = { count ->
                    quizViewModel.createQuiz(
                        "Quiz ${quizViewModel.quizzes.size + 1}",
                        count,
                        emptyList()
                    )
                    navController.navigate(Screen.QuizList.route)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.QuizList.route) {
            QuizListScreen(
                quizzes = quizViewModel.quizzes,
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
                onAdd = { quizIdx ->
                    quizViewModel.setCurrentQuiz(quizIdx)
                    navController.navigate(Screen.AddQuestion.route)
                },
                onAddQuiz = { navController.navigate(Screen.AddQuiz.route) },
                onRename = { index, name -> quizViewModel.renameQuiz(index, name) },
                onDelete = { index -> quizViewModel.deleteQuiz(index) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.QuizList.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onTab = { tab ->
                    when (tab) {
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                    }
                }
            )
        }
        composable(Screen.Folders.route) {
            FolderScreen(
                quizzes = quizViewModel.quizzes,
                onRenameQuiz = { index, name -> quizViewModel.renameQuiz(index, name) },
                onDeleteQuiz = { index -> quizViewModel.deleteQuiz(index) },
                onRenameCategory = { quizIdx, level, name -> quizViewModel.renameCategory(quizIdx, level, name) },
                onDeleteCategory = { quizIdx, level -> quizViewModel.deleteCategory(quizIdx, level) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.BoxList.route) {
            BoxListScreen(
                quizName = quizViewModel.currentQuizName,
                boxes = quizViewModel.boxes,
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
                onAdd = { navController.navigate(Screen.AddQuestion.route) },
                onView = { index -> navController.navigate(Screen.QuestionList.createRoute(index)) },
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.BoxList.route) { inclusive = true }
                    }
                },
                onTab = { tab ->
                    when (tab) {
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> navController.navigate(Screen.HomeFeed.route)
                    }
                }
            )
        }
        composable(Screen.AddQuestion.route) {
            AddQuestionScreen(
                boxCount = quizViewModel.boxes.size,
                onAdd = { q, a, topic, sub, box ->
                    quizViewModel.addQuestion(q, a, topic, sub, box)
                },
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }
        composable(Screen.AddQuiz.route) {
            AddQuizScreen(
                existingNames = quizViewModel.quizzes.map { it.name },
                onCreate = { name, count, categories ->
                    quizViewModel.createQuiz(name, count, categories)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
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
                onTab = { tab ->
                    when (tab) {
                        BottomTab.PROFILE -> navController.navigate(Screen.Profile.route)
                        BottomTab.HOME -> navController.navigate(Screen.QuizList.route)
                        BottomTab.EXPLORE -> {}
                    }
                }
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
                onQuit = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.QuestionList.route,
            arguments = listOf(navArgument(Screen.QuestionList.boxArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt(Screen.QuestionList.boxArg) ?: 0
            val questions = quizViewModel.boxes.getOrNull(index).orEmpty()
            QuestionListScreen(
                questions = questions,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
