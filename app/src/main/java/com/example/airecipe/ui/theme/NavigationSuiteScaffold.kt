package com.example.airecipe.ui.theme

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.airecipe.R
import com.example.airecipe.ui.theme.screens.FavouriteScreenDestination
import com.example.airecipe.ui.theme.screens.FeaturedRecipesScreenDestination
import com.example.airecipe.ui.theme.screens.HomeScreenDestination
import com.example.airecipe.ui.theme.screens.ProfileScreenDestination


enum class AppBarNavigationDestinations(
    val route: String, // ✅ Add route
    @StringRes val label: Int,
    val icon: Int,
    @StringRes val contentDescription: Int
) {
    HOME(
        HomeScreenDestination.route,
        R.string.home,
        R.drawable.baseline_home_mini_24,
        R.string.home
    ),
    RECIPES(
        FeaturedRecipesScreenDestination.route,
        R.string.recipe,
        R.drawable.baseline_food_bank_24,
        R.string.recipes
    ),
    FAVORITES(
        FavouriteScreenDestination.route,
        R.string.favorites,
        R.drawable.baseline_favorite_24,
        R.string.favorites
    ),
    PROFILE(
        ProfileScreenDestination.route,
        R.string.profile,
        R.drawable.baseline_account_box_24,
        R.string.profile
    );

    companion object {
        val all = entries.toTypedArray()
    }
}

data class NavigationItem(
    val title: String,
    val icon: Int,
    val route: String
)


@Composable
fun BottomNavigationBar(
    navController: NavController,
) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val navigationItems = listOf(
        NavigationItem(
            "Home",
            R.drawable.baseline_home_mini_24,
            HomeScreenDestination.route
        ),
        NavigationItem(
            "Featured",
            R.drawable.baseline_fastfood_24,
            FeaturedRecipesScreenDestination.route
        ),
        NavigationItem(
            "Favorites",
            R.drawable.baseline_favorite_24,
            FavouriteScreenDestination.route
        ),
        NavigationItem(
            "Profile",
            R.drawable.baseline_person_4_24,
            ProfileScreenDestination.route
        ),
    )

    NavigationBar(
        containerColor = ForestGreen.copy(alpha = 0.6f),
    ) {
        navigationItems.forEachIndexed { index, navigationItem ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(navigationItem.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(navigationItem.icon),
                        contentDescription = navigationItem.title
                    )
                },
                label = {
                    Text(
                        navigationItem.title,
                        color = if (index == selectedNavigationIndex.intValue)
                            Color.White
                        else
                            Color.Black
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surface,
                    selectedIconColor = ButtonColor
                )
            )
        }
    }

}

@Composable
fun NavigationSuiteScaffoldBar(
    modifier: Modifier = Modifier,
    navController: NavController, // ✅ Added navController
) {
    // Get current destination
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    // Items colors of navigation
    val myNavigationSuiteItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = ButtonColor.copy(0.3f),
            selectedIconColor = ButtonColor
        ),
    )

    // Adaptive screen
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppBarNavigationDestinations.entries.forEach { destination ->
                item(
                    selected = currentDestination?.route == destination.route,  // ✅ Compare correctly
                    onClick = {
                        if (currentDestination?.route != destination.route) {  // ✅ Prevent duplicate navigation
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(destination.label),
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(destination.icon),
                            contentDescription = stringResource(destination.contentDescription)
                        )
                    },
                    colors = myNavigationSuiteItemColors
                )
            }
        },
        layoutType = customNavSuiteType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = ButtonColor.copy(alpha = 0.1f)
        ),
    ) {
        Box(modifier = Modifier.padding()) { // ✅ Use `it` (default lambda parameter)
        }
    }

}
