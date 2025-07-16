package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_headings",
    foreignKeys = [
        ForeignKey(
            entity = UserQuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quizId")]
)
data class SubHeadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int,
    val name: String,
    val boxIndex: Int
)
