package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SettingsDao : BaseDao<SettingEntity> {
    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): String?
}
