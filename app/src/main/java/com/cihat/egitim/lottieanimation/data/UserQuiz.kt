package com.cihat.egitim.lottieanimation.data

/**
 * Represents a quiz owned by the current user. Each quiz holds its own boxes.
 */
data class UserQuiz(
    val id: Int,
    val name: String,
    val boxes: MutableList<MutableList<Question>>,
    /** Optional sub headings defined by the user */
    val subHeadings: MutableList<String> = mutableListOf(),
    /** Id of the folder this quiz belongs to */
    val folderId: Int? = null,
    /** Username of the quiz author if imported */
    val author: String? = null,
    /** Full name of the quiz author */
    val authorName: String? = null,
    /** Photo of the quiz owner if imported from another user */
    val authorPhotoUrl: String? = null,
    /** Whether this quiz was imported from another user */
    val isImported: Boolean = false,
)
