package com.example.airecipe.data.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) {

    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            Log.d("AuthRepository", "Attempting to sign up with email: $email")
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                user.sendEmailVerification().await()
                Log.d("AuthRepository", "Verification email sent to: $email")
                Result.success(user)
            } else {
                Log.e("AuthRepository", "User is null after sign up with email: $email")
                Result.failure(Exception("User is null after sign-up"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign up failed for email: $email", e)
            Result.failure(e)
        }
    }


    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return try {
            Log.d("AuthRepository", "Attempting to log in with email: $email")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                return if (user.isEmailVerified) {
                    Log.d("AuthRepository", "Login successful for email: $email")
                    Result.success(user)
                } else {
                    Log.w("AuthRepository", "Login failed: Email not verified for $email")
                    Result.failure(Exception("Email not verified. Please check your inbox."))
                }
            } else {
                Result.failure(Exception("Login failed. User not found."))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed for email: $email", e)
            Result.failure(e)
        }
    }


    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        Log.d("AuthRepository", "Attempting to sign in with Google ID token")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Log.d("AuthRepository", "Google sign-in successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google sign-in failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithPhone(credential: PhoneAuthCredential): Result<Unit> {
        Log.d("AuthRepository", "Attempting to sign in with phone credential")
        return try {
            firebaseAuth.signInWithCredential(credential).await()
            Log.d("AuthRepository", "Phone sign-in successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Phone sign-in failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}