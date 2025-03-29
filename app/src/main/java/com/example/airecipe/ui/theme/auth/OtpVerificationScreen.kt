package com.example.airecipe.ui.theme.auth

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.airecipe.R
import com.example.airecipe.model.AppNavigator
import com.example.airecipe.model.AuthState
import com.example.airecipe.ui.theme.ButtonColor
import com.example.airecipe.ui.theme.screens.HomeScreenDestination
import com.example.airecipe.ui_components.CustomButton
import com.example.airecipe.ui_components.CustomOutlinedTextField
import com.example.airecipe.ui_components.OtpTextField
import com.example.airecipe.ui_components.SocialAuthButtons
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay

object OtpVerificationScreenDestination : AppNavigator {
    override val title = "OTP Verification"
    override val route = "otp_Verification"
}


@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit,
    isVerifying: Boolean,
    timerSeconds: Int
) {
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Log.d("OtpVerificationScreen", "Screen Initialized with phone number: $phoneNumber")

    LaunchedEffect(Unit) {
        keyboardController?.show() // ✅ Show the keyboard when the screen appears
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Enter OTP", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "We have sent a code to $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // ✅ No need for `FocusRequester`
            OtpTextField(
                otp = otp,
                onOtpChange = {
                    Log.d("OtpVerificationScreen", "OTP entered: $it")
                    otp = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                onClick = {
                    Log.d("OtpVerificationScreen", "Verify OTP button clicked with OTP: $otp")
                    onVerifyOtp(otp)
                },
                enabled = otp.length == 6 && !isVerifying,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ButtonColor,
                        trackColor = ButtonColor.copy(0.7f)
                    )
                } else {
                    Text("Verify OTP")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (timerSeconds > 0) {
                Text("Resend OTP in $timerSeconds seconds", style = MaterialTheme.typography.bodySmall)
            } else {
                TextButton(onClick = {
                    Log.d("OtpVerificationScreen", "Resend OTP button clicked")
                    onResendOtp()
                }) {
                    Text("Resend OTP")
                }
            }
        }
    }

    if (isVerifying) {
        Log.d("OtpVerificationScreen", "Verifying OTP...")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ButtonColor,
                trackColor = ButtonColor.copy(0.7f)
            )
        }
    }
}



