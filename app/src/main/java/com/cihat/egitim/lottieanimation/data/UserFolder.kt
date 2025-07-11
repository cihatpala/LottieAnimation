package com.cihat.egitim.lottieanimation.data

/**
 * Represents a folder created by the user. Each folder can contain
 * a hierarchy of headings.
 */
import com.cihat.egitim.lottieanimation.data.FolderHeading

data class UserFolder(
    val id: Int,
    val name: String,
    /** Root level headings inside the folder */
    val headings: MutableList<FolderHeading> = mutableListOf()
)
