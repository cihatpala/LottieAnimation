package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class UserFolderEntity(
    @PrimaryKey val id: Int,
    val name: String
)
