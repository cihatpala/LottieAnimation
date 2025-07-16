package com.cihat.egitim.lottieanimation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserFolderEntity::class,
        FolderHeadingEntity::class,
        UserQuizEntity::class,
        QuestionEntity::class,
        SubHeadingEntity::class,
        SettingEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
