package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDao {
    @Query("SELECT * FROM folders")
    suspend fun getFolders(): List<UserFolderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<UserFolderEntity>)

    @Query("DELETE FROM folders")
    suspend fun clearFolders()

    @Query("SELECT * FROM folder_headings")
    suspend fun getHeadings(): List<FolderHeadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeadings(headings: List<FolderHeadingEntity>)

    @Query("DELETE FROM folder_headings")
    suspend fun clearHeadings()

    @Query("SELECT * FROM quizzes")
    suspend fun getQuizzes(): List<UserQuizEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<UserQuizEntity>)

    @Query("DELETE FROM quizzes")
    suspend fun clearQuizzes()

    @Query("SELECT * FROM sub_headings")
    suspend fun getSubHeadings(): List<SubHeadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubHeadings(subHeadings: List<SubHeadingEntity>)

    @Query("DELETE FROM sub_headings")
    suspend fun clearSubHeadings()

    @Query("SELECT * FROM questions")
    suspend fun getQuestions(): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()

    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putSetting(setting: SettingEntity)

    @Query("SELECT * FROM user_session LIMIT 1")
    suspend fun getUserSession(): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSession(session: UserSessionEntity)

    @Query("DELETE FROM user_session")
    suspend fun clearUserSession()
}
