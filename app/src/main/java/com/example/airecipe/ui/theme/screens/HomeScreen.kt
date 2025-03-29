package com.example.airecipe.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.airecipe.model.AppNavigator
import com.example.airecipe.model.AuthState
import com.example.airecipe.ui.theme.auth.AuthViewModel
import com.example.airecipe.ui.theme.auth.SignInScreenDestination


object HomeScreenDestination : AppNavigator {
    override val title = "Home"
    override val route = "home"
}
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val authState by authViewModel.authState.collectAsState()

    Log.d("HomeScreen", "AuthState: $authState")  // Check if state changes in UI

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState) {
            is AuthState.Success -> {
                val user = (authState as AuthState.Success).user
                Log.d("HomeScreen", "Displaying user: ${user?.displayName}, ${user?.email}")

                val displayName = user?.displayName ?: "User"
                val email = user?.email ?: "No Email"
                val photoUrl = user?.photoUrl?.toString()

                photoUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
                Text("Welcome, $displayName", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Email: $email", fontSize = 16.sp)
            }

            is AuthState.Loading -> {
                CircularProgressIndicator()
            }

            is AuthState.Error -> {
                Log.e("HomeScreen", "Error in authentication: ${(authState as AuthState.Error).message}")
                Text("Authentication Failed: ${(authState as AuthState.Error).message}", fontSize = 16.sp)
            }

            else -> {
                Log.d("HomeScreen", "Showing Not Signed In message")
                Text("Not signed in", fontSize = 16.sp)
            }
        }

        Button(
            onClick = {
                authViewModel.signOut()
                navController.navigate(SignInScreenDestination.route)
            }
        ) {
            Text(
                text = "Sign Out"
            )
        }
    }
}
