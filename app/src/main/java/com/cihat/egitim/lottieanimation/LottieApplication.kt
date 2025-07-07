package com.cihat.egitim.lottieanimation

import android.app.Application
import com.google.firebase.FirebaseApp

class LottieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
