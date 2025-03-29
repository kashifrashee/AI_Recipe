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
import com.example.airecipe.ui_components.SocialAuthButtons
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException


object SignInScreenDestination : AppNavigator {
    override val title = "Sign in"
    override val route = "sign_in"
}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    googleSignInClient: GoogleSignInClient,
    navController: NavController,
    onSignUpClick: () -> Unit,
) {

    Log.d("SignInScreen", "Composable Initialized")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    var isGoogleSigningIn by remember { mutableStateOf(false) }


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
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.icons8_allrecipes_200),
            contentDescription = "App Icon",
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Sign in your account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 50.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        CustomOutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            isError = emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
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
            },
            isError = passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomButton(
            onClick = {
                Log.d("Sign in Validation", "Email: $email")
                Log.d("Sign in Validation", "Password: $password")

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    authViewModel.signInWithEmail(email, password, context, navController)
                } else {
                    emailError = email.isEmpty()
                    passwordError = password.isEmpty()
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
                Text("SIGN IN", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("or sign in with", style = MaterialTheme.typography.bodyMedium)
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
                    text = "Sign-in with Google",
                    color = ButtonColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // These are thow buttons with phone and google sign in
        /*SocialAuthButtons(
            onGoogleClick = {
                isGoogleSigningIn = true
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            onMobileClick = { onMobileClick() }
        )*/

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Have an account? ", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "SIGN UP",
                style = MaterialTheme.typography.bodyMedium.copy(color = ButtonColor),
                modifier = Modifier.clickable { onSignUpClick() }
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
}

@Preview
@Composable
private fun Previews() {
    OutlinedButton(
        onClick = {

        },
        border = BorderStroke(
            1.dp, ButtonColor
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
                text = "Sign-in with Google",
                color = ButtonColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}