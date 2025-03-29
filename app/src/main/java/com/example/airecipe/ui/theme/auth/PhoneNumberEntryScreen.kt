package com.example.airecipe.ui.theme.auth


import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.airecipe.model.AppNavigator
import com.example.airecipe.ui.theme.ButtonColor
import com.example.airecipe.ui_components.CustomButton
import com.example.airecipe.ui_components.CustomOutlinedTextField

object PhoneNumberEntryScreenDestination : AppNavigator {
    override val title = "Phone Number Entry"
    override val route = "phone_number_entry"
}


@Composable
fun PhoneNumberEntryScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onNext: (String) -> Unit,
    isLoading: Boolean
) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Enter Your Phone Number", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    if (newValue.length <= 11 && newValue.all { it.isDigit() }) {
                        phoneNumber = newValue
                    }
                },
                label = "Phone Number",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                onClick = {
                    if (phoneNumber.length == 11) {
                        Log.d("PhoneNumberEntry", "Sending OTP to $phoneNumber")

                        val formattedPhoneNumber = if (phoneNumber.startsWith("+")) {
                            phoneNumber // Already in correct format
                        } else {
                            "+92${phoneNumber.removePrefix("0")}" // Convert 0349... → +92349...
                        }

                        authViewModel.sendOtp(formattedPhoneNumber, activity = context as Activity) // ✅ Send OTP here
                        Log.d("PhoneNumberEntry", "OTP send to $phoneNumber")
                        onNext(phoneNumber)
                    } else {
                        Toast.makeText(
                            context,
                            "Enter a valid 11-digit phone number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ButtonColor,
                        trackColor = ButtonColor.copy(0.7f)
                    )
                } else {
                    Text("Next")
                }
            }
        }
    }
}
