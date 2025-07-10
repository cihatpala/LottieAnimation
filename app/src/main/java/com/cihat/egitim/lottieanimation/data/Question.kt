package com.cihat.egitim.lottieanimation.data

/**
 * Data model that represents a single question and its answer.
 */
data class Question(
    val text: String,
    val answer: String,
    val topic: String = "",
    val subtopic: String = ""
)
