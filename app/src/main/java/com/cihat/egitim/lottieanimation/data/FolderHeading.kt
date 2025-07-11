package com.cihat.egitim.lottieanimation.data

/**
 * Represents a heading inside a folder which can contain nested headings.
 */
data class FolderHeading(
    val id: Int,
    val name: String,
    val children: MutableList<FolderHeading> = mutableListOf()
)

