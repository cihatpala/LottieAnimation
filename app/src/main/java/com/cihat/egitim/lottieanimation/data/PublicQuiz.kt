package com.cihat.egitim.lottieanimation.data

/**
 * Represents a public quiz shared by another user.
 */
data class PublicQuiz(
    val name: String,
    val author: String,
    val questions: List<Question>
)
