package com.cihat.egitim.lottieanimation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cihat.egitim.lottieanimation.data.Question
import kotlin.math.min

/**
 * ViewModel that holds the quiz state with dynamic boxes.
 */
class QuizViewModel : ViewModel() {

    var uiState: UiState by mutableStateOf(UiState.SetupBoxes)
        private set

    var boxes: MutableList<MutableList<Question>> = mutableListOf()
        private set

    private var currentBoxIndex by mutableStateOf(0)
    private var currentQuestionIndex by mutableStateOf(0)

    var isAnswerVisible by mutableStateOf(false)
        private set

    /** Sets the desired number of boxes and moves to box list screen */
    fun setBoxCount(count: Int) {
        if (count <= 0) return
        boxes = MutableList(count) { mutableListOf() }
        uiState = UiState.BoxList
    }

    /** Navigates to Add Question screen */
    fun toAddQuestion() { uiState = UiState.AddQuestion }

    /** Returns the current question in quiz mode */
    val currentQuestion: Question?
        get() = boxes.getOrNull(currentBoxIndex)?.getOrNull(currentQuestionIndex)

    /** Adds a new question to the first box */
    fun addQuestion(text: String, answer: String) {
        if (boxes.isEmpty()) return
        boxes[0].add(Question(text, answer))
    }

    /** Starts quiz for the given box index */
    fun startQuiz(index: Int) {
        if (boxes.getOrNull(index).isNullOrEmpty()) return
        currentBoxIndex = index
        currentQuestionIndex = 0
        isAnswerVisible = false
        uiState = UiState.Quiz(index)
    }

    /** Reveals the current answer */
    fun revealAnswer() { isAnswerVisible = true }

    /** Processes the user answer and moves question between boxes */
    fun onAnswerSelected(correct: Boolean) {
        val box = boxes[currentBoxIndex]
        val question = box.removeAt(currentQuestionIndex)
        val nextIndex = if (correct) min(currentBoxIndex + 1, boxes.lastIndex) else 0
        boxes[nextIndex].add(question)
        isAnswerVisible = false
        if (box.isEmpty()) {
            uiState = UiState.BoxList
        } else {
            if (currentQuestionIndex >= box.size) currentQuestionIndex = 0
        }
    }

    /** Returns to box list screen */
    fun backToBoxes() { uiState = UiState.BoxList }
}
