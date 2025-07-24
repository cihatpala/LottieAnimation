package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserSessionDao : BaseDao<UserSessionEntity> {
    @Query("SELECT * FROM user_session LIMIT 1")
    suspend fun getUserSession(): UserSessionEntity?

    @Query("DELETE FROM user_session")
    suspend fun clearUserSession()
}
