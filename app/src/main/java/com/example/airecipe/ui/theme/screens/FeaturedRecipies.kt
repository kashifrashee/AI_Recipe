package com.example.airecipe.ui.theme.screens


import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.airecipe.data.repository.Recipe
import com.example.airecipe.model.AppNavigator
import com.example.airecipe.ui.theme.ButtonColor
import com.example.airecipe.ui.theme.screens.homeScreenComponents.FeaturedRecipes
import com.example.airecipe.ui.theme.screens.homeScreenComponents.HomeViewModel
import com.example.airecipe.ui.theme.screens.homeScreenComponents.RecipeGrid
import com.example.airecipe.ui.theme.screens.homeScreenComponents.SearchBar


object FeaturedRecipesScreenDestination : AppNavigator {
    override val title = "Featured Recipes"
    override val route = "featured_recipes"
}

@Composable
fun FeaturedRecipesScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRecipeClick: (Recipe) -> Unit
) {
    val featuredRecipes by viewModel.featuredRecipes.collectAsState()
    val searchedRecipes by viewModel.searchedRecipes.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(featuredRecipes) {
        if (viewModel.featuredRecipes.value.isEmpty()) {
            Log.d("FeaturedRecipesScreen", "Triggering fetchFeaturedRecipes()")
            viewModel.fetchFeaturedRecipes()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = ButtonColor,
                    trackColor = ButtonColor.copy(0.5f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Featured Recipes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                if (featuredRecipes.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No results",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "No results found!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else {
                    FeaturedRecipes(
                        recipes = featuredRecipes,
                        onRecipeClick = onRecipeClick
                    )
                }
            }
        }
    }
}

