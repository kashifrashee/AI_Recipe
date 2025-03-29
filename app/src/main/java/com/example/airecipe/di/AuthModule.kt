package com.example.airecipe.di

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        Log.d("AuthModule", "Providing FirebaseAuth instance")
        val firebaseAuth = FirebaseAuth.getInstance()
        Log.d("AuthModule", "FirebaseAuth instance provided: $firebaseAuth")
        return firebaseAuth
    }

    @Provides
    @Singleton
    fun provideGoogleSigInClient(@ApplicationContext context: Context): GoogleSignInClient {
        Log.d("AuthModule", "Providing GoogleSignInClient instance")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("769844011307-371bn8539b7ks61lm7ed4jpekfno3kk7.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        Log.d("AuthModule", "GoogleSignInClient instance provided: $googleSignInClient")
        return googleSignInClient
    }
// 769844011307-tf0qfq1fpe1nfdv71a3lcmk8m3tmiftd.apps.googleusercontent.com"
    @Provides
    @Singleton
    fun providePhoneAuthProvider(): PhoneAuthProvider {
        Log.d("AuthModule", "Providing PhoneAuthProvider instance")
        val phoneAuthProvider = PhoneAuthProvider.getInstance()
        Log.d("AuthModule", "PhoneAuthProvider instance provided: $phoneAuthProvider")
        return phoneAuthProvider
    }
}
