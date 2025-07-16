package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quizzes",
    foreignKeys = [
        ForeignKey(
            entity = UserFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("folderId")]
)
data class UserQuizEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val folderId: Int? = null
)
