package com.cihat.egitim.lottieanimation.viewmodel

import android.content.Context
import com.cihat.egitim.lottieanimation.utils.NetworkUtils
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.cihat.egitim.lottieanimation.data.StoredUser
import com.cihat.egitim.lottieanimation.data.local.LocalRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: LocalRepository) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    /** Currently authenticated Firebase user, or null when logged out */
    var currentUser: FirebaseUser? by mutableStateOf(auth.currentUser)
        private set

    /** User info persisted locally when auth is unavailable */
    var storedUser: StoredUser? by mutableStateOf(null)
        private set

    private val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        currentUser = firebaseAuth.currentUser
        viewModelScope.launch {
            if (currentUser != null) {
                val u = currentUser!!
                val stored = StoredUser(
                    uid = u.uid,
                    name = u.displayName,
                    email = u.email,
                    photoUrl = u.photoUrl?.toString()
                )
                repository.saveUserSession(stored)
                storedUser = stored
            } else {
                repository.saveUserSession(null)
                storedUser = null
            }
        }
    }

    init {
        viewModelScope.launch { storedUser = repository.loadUserSession() }
        auth.addAuthStateListener(listener)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(listener)
        super.onCleared()
    }

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
        viewModelScope.launch {
            repository.saveUserSession(null)
            storedUser = null
        }
    }

}
