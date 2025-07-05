package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}
