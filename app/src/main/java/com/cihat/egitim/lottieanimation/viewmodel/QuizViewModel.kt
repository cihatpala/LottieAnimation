package com.cihat.egitim.lottieanimation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cihat.egitim.lottieanimation.data.Question

/**
 * ViewModel that holds the quiz state.
 */
class QuizViewModel : ViewModel() {

    // Static list of 10 question-answer pairs
    private val questions: List<Question> = listOf(
        Question("Question 1", "Answer 1"),
        Question("Question 2", "Answer 2"),
        Question("Question 3", "Answer 3"),
        Question("Question 4", "Answer 4"),
        Question("Question 5", "Answer 5"),
        Question("Question 6", "Answer 6"),
        Question("Question 7", "Answer 7"),
        Question("Question 8", "Answer 8"),
        Question("Question 9", "Answer 9"),
        Question("Question 10", "Answer 10")
    )

    var currentIndex by mutableStateOf(0)
        private set

    var isAnswerVisible by mutableStateOf(false)
        private set

    /** Returns the current question */
    val currentQuestion: Question
        get() = questions[currentIndex]

    /** Called when the user clicks to reveal the answer */
    fun revealAnswer() {
        isAnswerVisible = true
    }

    /** Called when the user selects either correct or incorrect */
    fun onAnswerSelected() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
        } else {
            currentIndex = 0
        }
        isAnswerVisible = false
    }
}
