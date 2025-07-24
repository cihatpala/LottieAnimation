package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores minimal user info to persist authentication state locally.
 */
@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)
