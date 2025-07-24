package com.cihat.egitim.lottieanimation.data

/**
 * Minimal user information persisted locally for offline sessions.
 */
data class StoredUser(
    val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)
