package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizDao : BaseDao<UserQuizEntity> {
    @Query("SELECT * FROM quizzes")
    suspend fun getQuizzes(): List<UserQuizEntity>

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
}
