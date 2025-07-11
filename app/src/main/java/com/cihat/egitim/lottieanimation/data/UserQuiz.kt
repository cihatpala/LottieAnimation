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
    val folderId: Int? = null
)
