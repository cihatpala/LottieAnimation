package com.cihat.egitim.lottieanimation.viewmodel

import android.content.Context
import com.cihat.egitim.lottieanimation.utils.NetworkUtils
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    val currentUser
        get() = auth.currentUser

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false)
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun register(context: Context, email: String, password: String, onResult: (Boolean) -> Unit) {
        if (!NetworkUtils.isConnected(context)) {
            onResult(false)
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun logout(context: Context) {
        AuthUI.getInstance().signOut(context)
    }

}
