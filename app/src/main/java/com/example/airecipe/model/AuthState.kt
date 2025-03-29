package com.example.airecipe.model

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState() {
        val displayName: String? get() = user?.displayName
        val photoUrl: Uri? get() = user?.photoUrl
        val email: String? get() = user?.email
    }
    data class Error(val message: String) : AuthState()
}