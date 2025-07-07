package com.cihat.egitim.lottieanimation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.data.PublicQuiz
import kotlin.math.min

/**
 * ViewModel that holds the quiz state with dynamic boxes.
 */
class QuizViewModel : ViewModel() {

    var boxes: MutableList<MutableList<Question>> = mutableListOf()
        private set

    /** Sample public quizzes that could come from a backend in a real app */
    val publicQuizzes: List<PublicQuiz> = listOf(
        PublicQuiz(
            name = "Capital Cities",
            author = "Alice",
            questions = listOf(
                Question("Capital of France?", "Paris"),
                Question("Capital of Spain?", "Madrid")
            )
        ),
        PublicQuiz(
            name = "Math Basics",
            author = "Bob",
            questions = listOf(
                Question("2 + 2?", "4"),
                Question("5 * 3?", "15")
            )
        )
    )

    private var currentBoxIndex by mutableStateOf(0)
    private var currentQuestionIndex by mutableStateOf(0)

    var isAnswerVisible by mutableStateOf(false)
        private set

    /** Sets the desired number of boxes */
    fun setBoxCount(count: Int) {
        if (count <= 0) return
        boxes = MutableList(count) { mutableListOf() }
    }

    /** Imports all questions from a public quiz into the first box */
    fun importQuiz(quiz: PublicQuiz) {
        if (boxes.isEmpty()) return
        boxes[0].addAll(quiz.questions.map { it.copy() })
    }

    /** Returns the current question in quiz mode */
    val currentQuestion: Question?
        get() = boxes.getOrNull(currentBoxIndex)?.getOrNull(currentQuestionIndex)

    /** Adds a new question to the given box index */
    fun addQuestion(text: String, answer: String, topic: String, subtopic: String, boxIndex: Int) {
        boxes.getOrNull(boxIndex)?.add(Question(text, answer, topic, subtopic))
    }

    /** Starts quiz for the given box index */
    fun startQuiz(index: Int) {
        if (boxes.getOrNull(index).isNullOrEmpty()) return
        currentBoxIndex = index
        currentQuestionIndex = 0
        isAnswerVisible = false
    }

    /** Reveals the current answer */
    fun revealAnswer() { isAnswerVisible = true }

    /** Processes the user answer and moves question between boxes.
     *  Returns true if there are more questions to ask.
     */
    fun onAnswerSelected(correct: Boolean): Boolean {
        val box = boxes[currentBoxIndex]
        val question = box.removeAt(currentQuestionIndex)
        val nextIndex = if (correct) min(currentBoxIndex + 1, boxes.lastIndex) else 0
        boxes[nextIndex].add(question)
        isAnswerVisible = false
        if (box.isEmpty()) {
            return false
        } else {
            if (currentQuestionIndex >= box.size) currentQuestionIndex = 0
            return true
        }
    }
}
