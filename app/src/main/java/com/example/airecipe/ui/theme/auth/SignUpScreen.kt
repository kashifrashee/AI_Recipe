package com.example.airecipe.ui.theme.auth

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.airecipe.ui_components.NavigationAppBar
import com.example.airecipe.ui_components.TermsAndConditionsDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException


object SignUpScreenDestination : AppNavigator {
    override val title = "Sign Up"
    override val route = "sign_up"
}

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    googleSignInClient: GoogleSignInClient,
    onSignInClick: () -> Unit,
    onTermsAndPolicyClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptedTerms by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val isValidEmail = email.contains("@") && email.contains(".")
    val isValidPassword = password.length >= 8
    val passwordsMatch = password == confirmPassword
    val isFormValid = isValidEmail && isValidPassword && passwordsMatch && acceptedTerms
    val context = LocalContext.current

    var isGoogleSigningIn by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Launcher to start Google Sign-In Intent
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isGoogleSigningIn = true
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                authViewModel.signInWithGoogle(idToken, navController)
            }
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Google sign-in failed", e)
            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
        } finally {
            isGoogleSigningIn = false
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Log.d("Navigation", "Navigating to HomeScreen")
                navController.navigate(HomeScreenDestination.route) {
                    popUpTo(SignInScreenDestination.route) {
                        inclusive = true
                    } // Removes Sign-In from stack
                }
            }

            is AuthState.Error -> {
                val errorState = authState as AuthState.Error
                Toast.makeText(context, errorState.message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NavigationAppBar(
            modifier = Modifier,
            onBackClick = {}
        )

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 50.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        CustomOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            isError = email.isNotEmpty() && !isValidEmail,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = if (passwordVisible) painterResource(R.drawable.baseline_visibility_24) else painterResource(
                            R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null,
                        tint = ButtonColor
                    )
                }
            }, keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = password.isNotEmpty() && !isValidPassword,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = if (confirmPasswordVisible) painterResource(R.drawable.baseline_visibility_24) else painterResource(
                            R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null,
                        tint = ButtonColor
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPassword.isNotEmpty() && !passwordsMatch,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = ButtonColor.copy(0.5f),
                    checkmarkColor = ButtonColor
                )
            )
            Text("I understand the ", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "terms & policy",
                style = MaterialTheme.typography.bodySmall.copy(color = ButtonColor),
                modifier = Modifier.clickable {
                    showTermsDialog = true
                }
            )
        }

        CustomButton(
            onClick = {
                Log.d("Validation", "Email: $email")
                Log.d("Validation", "Password: $password")
                Log.d("Validation", "Confirm Password: $confirmPassword")
                Log.d("Validation", "isValidEmail: $isValidEmail")
                Log.d("Validation", "isValidPassword: $isValidPassword")
                Log.d("Validation", "passwordsMatch: $passwordsMatch")
                Log.d("Validation", "acceptedTerms: $acceptedTerms")
                Log.d("Validation", "isFormValid: $isFormValid")

                if (isFormValid && authState !is AuthState.Error) {
                    authViewModel.signUpWithEmail(email, password, context)

                    // Clear fields after successful sign-up
                    email = ""
                    password = ""
                    confirmPassword = ""
                } else {
                    Toast.makeText(context, "Fill the empty fields", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = ButtonColor,
                    trackColor = ButtonColor.copy(0.7f)
                )
            } else {
                Text("SIGN UP", color = Color.White)
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text("or sign up with", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                isGoogleSigningIn = true
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            border = BorderStroke(
                2.dp, ButtonColor
            ),
            elevation = ButtonDefaults.buttonElevation(
                pressedElevation = 2.dp,
                hoveredElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icons8_google_48),
                    contentDescription = "Google",
                )
                Text(
                    text = "Sign-up with Google",
                    color = ButtonColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        /*SocialAuthButtons(
            onGoogleClick = { onGoogleClick() },
            onMobileClick = { onMobileClick() }
        )*/

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Have an account? ", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "SIGN IN",
                style = MaterialTheme.typography.bodyMedium.copy(color = ButtonColor),
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }

    if (isGoogleSigningIn) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = ButtonColor,
                trackColor = ButtonColor.copy(0.5f)
            )
        }
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(
            onDismiss = {
                showTermsDialog = false
                acceptedTerms = true
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {

}