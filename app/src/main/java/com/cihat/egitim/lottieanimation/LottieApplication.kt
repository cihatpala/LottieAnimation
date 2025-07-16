package com.cihat.egitim.lottieanimation

import android.app.Application
import androidx.room.Room
import com.cihat.egitim.lottieanimation.data.local.AppDatabase
import com.cihat.egitim.lottieanimation.data.local.LocalRepository
import com.google.firebase.FirebaseApp

class LottieApplication : Application() {
    lateinit var repository: LocalRepository
        private set
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "app.db").build()
        repository = LocalRepository(db)
    }
}
