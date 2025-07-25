package com.cihat.egitim.lottieanimation.data

/**
 * Represents a public quiz shared by another user.
 */
data class PublicQuiz(
    val name: String,
    /** Username of the quiz author */
    val author: String,
    /** Full display name of the author */
    val authorName: String,
    val questions: List<Question>,
    val authorPhotoUrl: String? = null,
    val folderName: String? = null
)
