package com.example.airecipe.ui.theme.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.airecipe.data.repository.AuthRepository
import com.example.airecipe.model.AuthState
import com.example.airecipe.ui.theme.screens.HomeScreenDestination
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth, // Injected FirebaseAuth
    val googleSignInClient: GoogleSignInClient // Injected GoogleSignInClient
) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    var verificationId: String? = null
        private set

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val user = auth.currentUser
            Log.d("AuthViewModel", "FirebaseAuth state changed: $user")
            _authState.value = if (user != null) AuthState.Success(user) else AuthState.Idle
        }
    }

    fun signUpWithEmail(email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Sign up started for email: $email")
                _authState.value = AuthState.Loading

                val result = authRepository.signUpWithEmail(email, password)

                if (result.isSuccess) {
                    Log.d("AuthViewModel", "Sign up successful for email: $email")
                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                    _authState.value = AuthState.Success(result.getOrNull())

                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e("AuthViewModel", "Sign up failed for email: $email, error: $errorMessage")
                    Toast.makeText(context, "Sign up failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Sign up failed", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signInWithEmail(email: String, password: String, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Login started for email: $email")
                _authState.value = AuthState.Loading
                val result = authRepository.signInWithEmail(email, password)

                if (result.isSuccess) {
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Login successful for email: $email, Firebase User: $user")

                    _authState.value = AuthState.Success(user)
                    Log.d(TAG, "AuthState updated: ${_authState.value}")  // Check if auth state updates
                    navController.navigate(HomeScreenDestination.route) {
                        popUpTo(SignInScreenDestination.route) {
                            inclusive = true
                        }
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e(TAG, "Login failed: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signInWithGoogle(idToken: String, navController: NavController) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Google sign-in started with token: $idToken")
                _authState.value = AuthState.Loading

                val result = authRepository.signInWithGoogle(idToken)
                if (result.isSuccess) {
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Google sign-in successful: ${user?.displayName}, ${user?.email}, ${user?.photoUrl}")

                    _authState.value = AuthState.Success(user)
                    Log.d(TAG, "AuthState updated: ${_authState.value}")  // Check if auth state updates
                    navController.navigate(HomeScreenDestination.route) {
                        popUpTo(SignInScreenDestination.route) {
                            inclusive = true
                        }
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e(TAG, "Google sign-in failed: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Google sign-in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signInWithPhone(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Log.d(TAG, "Phone sign-in started with credential: $credential")
                val result = authRepository.signInWithPhone(credential)
                if (result.isSuccess) {
                    Log.d(TAG, "Phone sign-in successful with credential: $credential")
                    _authState.value = AuthState.Success(result.getOrNull() as FirebaseUser?)
                } else {
                    Log.e(TAG, "Phone sign-in failed with credential: $credential, error: ${result.exceptionOrNull()?.message}")
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Phone sign-in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun sendOtp(phoneNumber: String, activity: Activity) {
        Log.d(TAG, "Sending OTP to $phoneNumber")
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d(TAG, "FirebaseAuth: Auto OTP retrieval completed")
                    signInWithPhone(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "FirebaseAuth: Verification failed: ${e.message}")
                }

                override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d(TAG, "FirebaseAuth: OTP Sent. Verification ID: $id")
                    verificationId = id // ✅ Store the verification ID
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String) {
        val id = verificationId
        if (id != null) {
            val credential = PhoneAuthProvider.getCredential(id, otp)
            signInWithPhone(credential)
        } else {
            Log.e(TAG, "OtpVerification: Verification ID is null. Cannot verify OTP.")
        }
    }


    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
    }
}