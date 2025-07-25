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
        SettingEntity::class,
        UserSessionEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun quizDao(): QuizDao
    abstract fun settingsDao(): SettingsDao
    abstract fun sessionDao(): UserSessionDao
}
