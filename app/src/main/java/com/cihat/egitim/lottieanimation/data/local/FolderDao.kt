package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FolderDao : BaseDao<UserFolderEntity> {
    @Query("SELECT * FROM folders")
    suspend fun getFolders(): List<UserFolderEntity>

    @Query("DELETE FROM folders")
    suspend fun clearFolders()

    @Query("SELECT * FROM folder_headings")
    suspend fun getHeadings(): List<FolderHeadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeadings(headings: List<FolderHeadingEntity>)

    @Query("DELETE FROM folder_headings")
    suspend fun clearHeadings()
}
