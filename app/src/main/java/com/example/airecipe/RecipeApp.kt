package com.example.airecipe

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airecipe.ui.theme.auth.AuthViewModel
import com.example.airecipe.ui.theme.auth.OtpVerificationScreen
import com.example.airecipe.ui.theme.auth.OtpVerificationScreenDestination
import com.example.airecipe.ui.theme.auth.PhoneNumberEntryScreen
import com.example.airecipe.ui.theme.auth.PhoneNumberEntryScreenDestination
import com.example.airecipe.ui.theme.auth.SignInScreen
import com.example.airecipe.ui.theme.auth.SignInScreenDestination
import com.example.airecipe.ui.theme.auth.SignUpScreen
import com.example.airecipe.ui.theme.auth.SignUpScreenDestination
import com.example.airecipe.ui.theme.screens.HomeScreen
import com.example.airecipe.ui.theme.screens.HomeScreenDestination
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun RecipeApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    googleSignInClient: GoogleSignInClient = hiltViewModel<AuthViewModel>().googleSignInClient
) {

    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance().currentUser
    val startDestination = if (auth != null) {
        HomeScreenDestination.route
    } else {
        SignInScreenDestination.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = SignUpScreenDestination.route) {
            SignUpScreen(
                onSignInClick = {
                    Log.d("Navigation", "Navigating to SignUpScreen")
                    navController.navigate(SignInScreenDestination.route)
                },
                navController = navController,
                googleSignInClient = googleSignInClient,
                onTermsAndPolicyClick = {}
            )
        }

        composable(route = SignInScreenDestination.route) {
            SignInScreen(
                navController = navController,
                googleSignInClient = googleSignInClient,
                onSignUpClick = {
                    Log.d("Navigation", "Navigating to SignUpScreen")
                    navController.navigate(SignUpScreenDestination.route)
                },
            )
        }

        composable(route = HomeScreenDestination.route) {
            HomeScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        composable(route = PhoneNumberEntryScreenDestination.route) {
            PhoneNumberEntryScreen(
                isLoading = false,
                onNext = { phoneNumber ->
                    Log.d("PhoneNumberEntry", "Proceeding with phone number: $phoneNumber")
                    navController.navigate(OtpVerificationScreenDestination.route + "/$phoneNumber")
                }
            )
        }

        composable(
            route = OtpVerificationScreenDestination.route + "/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber"){type = NavType.StringType})
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            var timerSeconds by remember { mutableIntStateOf(60) }
            var isVerifying  by remember { mutableStateOf(false) }

            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                timerSeconds = timerSeconds,
                isVerifying = isVerifying,
                onVerifyOtp = { otp ->
                    Log.d("OtpVerification", "Verifying OTP: $otp")
                    isVerifying = true
                    authViewModel.verifyOtp(otp)
                },
                onResendOtp = {
                    Log.d("OtpVerification", "Resending OTP to: $phoneNumber")
                    timerSeconds = 60 // Reset the timer

                    authViewModel.sendOtp(phoneNumber, activity = context as Activity)
                }
            )
        }

    }

}