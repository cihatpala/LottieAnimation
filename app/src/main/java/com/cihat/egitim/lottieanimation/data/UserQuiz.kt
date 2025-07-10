package com.cihat.egitim.lottieanimation.data

/**
 * Represents a quiz owned by the current user. Each quiz holds its own boxes.
 */
data class UserQuiz(
    val id: Int,
    val name: String,
    val boxes: MutableList<MutableList<Question>>
)
