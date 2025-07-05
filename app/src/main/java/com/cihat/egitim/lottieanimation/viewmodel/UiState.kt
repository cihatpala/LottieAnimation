package com.cihat.egitim.lottieanimation.viewmodel

/**
 * Represents all screens of the quiz app.
 */
sealed interface UiState {
    data object SetupBoxes : UiState
    data object BoxList : UiState
    data object AddQuestion : UiState
    data class Quiz(val boxIndex: Int) : UiState
}
