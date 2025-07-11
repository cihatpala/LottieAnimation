package com.cihat.egitim.lottieanimation.data

/**
 * Represents a folder created by the user containing optional sub headings.
 */
data class UserFolder(
    val id: Int,
    val name: String,
    val subHeadings: MutableList<String> = mutableListOf()
)
