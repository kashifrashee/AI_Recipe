package com.example.airecipe

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airecipe.ui.theme.BottomNavigationBar
import com.example.airecipe.ui.theme.NavigationSuiteScaffoldBar
import com.example.airecipe.ui.theme.auth.AuthViewModel
import com.example.airecipe.ui.theme.auth.OtpVerificationScreen
import com.example.airecipe.ui.theme.auth.OtpVerificationScreenDestination
import com.example.airecipe.ui.theme.auth.PhoneNumberEntryScreen
import com.example.airecipe.ui.theme.auth.PhoneNumberEntryScreenDestination
import com.example.airecipe.ui.theme.auth.SignInScreen
import com.example.airecipe.ui.theme.auth.SignInScreenDestination
import com.example.airecipe.ui.theme.auth.SignUpScreen
import com.example.airecipe.ui.theme.auth.SignUpScreenDestination
import com.example.airecipe.ui.theme.screens.FavouriteScreen
import com.example.airecipe.ui.theme.screens.FavouriteScreenDestination
import com.example.airecipe.ui.theme.screens.FeaturedRecipesScreen
import com.example.airecipe.ui.theme.screens.FeaturedRecipesScreenDestination
import com.example.airecipe.ui.theme.screens.HomeScreen
import com.example.airecipe.ui.theme.screens.HomeScreenDestination
import com.example.airecipe.ui.theme.screens.ProfileScreen
import com.example.airecipe.ui.theme.screens.ProfileScreenDestination
import com.example.airecipe.ui.theme.screens.homeScreenComponents.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RecipeApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    googleSignInClient: GoogleSignInClient = hiltViewModel<AuthViewModel>().googleSignInClient
) {

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance().currentUser

    val startDestination = if (auth != null) {
        HomeScreenDestination.route
    } else {
        SignInScreenDestination.route
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute !in listOf(
                    SignUpScreenDestination.route,
                    SignInScreenDestination.route,
                )
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(route = SignUpScreenDestination.route) {
                SignUpScreen(
                    onSignInClick = { navController.navigate(SignInScreenDestination.route) },
                    navController = navController,
                    googleSignInClient = googleSignInClient,
                    onTermsAndPolicyClick = {}
                )
            }

            composable(route = SignInScreenDestination.route) {
                SignInScreen(
                    navController = navController,
                    googleSignInClient = googleSignInClient,
                    onSignUpClick = { navController.navigate(SignUpScreenDestination.route) }
                )
            }

            composable(route = HomeScreenDestination.route) {
                HomeScreen(
                    onRecipeClick = { recipe ->

                    }
                )
            }

            composable(route = FeaturedRecipesScreenDestination.route) {
                FeaturedRecipesScreen(
                    viewModel = homeViewModel,
                    onRecipeClick = { recipe ->
                    }
                )
            }

            composable(route = FavouriteScreenDestination.route) {
                FavouriteScreen()
            }

            composable(route = ProfileScreenDestination.route) {
                ProfileScreen(navController = navController, authViewModel = authViewModel)
            }

            composable(route = PhoneNumberEntryScreenDestination.route) {
                PhoneNumberEntryScreen(
                    isLoading = false,
                    onNext = { phoneNumber ->
                        navController.navigate(OtpVerificationScreenDestination.route + "/$phoneNumber")
                    }
                )
            }

            composable(
                route = OtpVerificationScreenDestination.route + "/{phoneNumber}",
                arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
            ) { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

                var timerSeconds by remember { mutableIntStateOf(60) }
                var isVerifying by remember { mutableStateOf(false) }

                OtpVerificationScreen(
                    phoneNumber = phoneNumber,
                    timerSeconds = timerSeconds,
                    isVerifying = isVerifying,
                    onVerifyOtp = { otp ->
                        isVerifying = true
                        authViewModel.verifyOtp(otp)
                    },
                    onResendOtp = {
                        timerSeconds = 60 // Reset the timer
                        authViewModel.sendOtp(phoneNumber, activity = context as Activity)
                    }
                )
            }
        }
    }


}


@Composable
private fun navigationSuiteItems(
    currentDestination: NavDestination?,
    navHost: NavHostController
): NavigationSuiteScope.() -> Unit = {
    item(
        selected = currentDestination?.hierarchy?.any { it.route == HomeScreenDestination.route } == true,
        onClick = {
            navigateWithBackStackHandling(HomeScreenDestination.route, navHost)
        },
        label = { Text("Home") },
        icon = {
            Icon(
                painter = painterResource(R.drawable.baseline_home_24), contentDescription = "home"
            )
        },
    )

    item(
        selected = currentDestination?.hierarchy?.any { it.route == FavouriteScreenDestination.route } == true,
        onClick = {
            navigateWithBackStackHandling(FavouriteScreenDestination.route, navHost)
        },
        label = { Text("Favourite") },
        icon = {
            Icon(
                painter = painterResource(R.drawable.baseline_favorite_24),
                contentDescription = "favourite"
            )
        },
    )

    item(
        selected = currentDestination?.hierarchy?.any { it.route == ProfileScreenDestination.route } == true,
        onClick = {
            navigateWithBackStackHandling(ProfileScreenDestination.route, navHost)
        },
        label = { Text("Favourite") },
        icon = {
            Icon(
                painter = painterResource(R.drawable.baseline_account_box_24),
                contentDescription = "profile"
            )
        },
    )
}

fun navigateWithBackStackHandling(route: String, navHost: NavHostController) {
    navHost.navigate(route) {
        popUpTo(navHost.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}