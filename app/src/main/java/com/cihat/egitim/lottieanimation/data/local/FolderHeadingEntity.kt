package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "folder_headings",
    foreignKeys = [
        ForeignKey(
            entity = UserFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FolderHeadingEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("folderId"), Index("parentId")]
)
data class FolderHeadingEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val folderId: Int,
    val parentId: Int? = null
)
