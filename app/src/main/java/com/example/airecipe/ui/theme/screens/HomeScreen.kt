package com.example.airecipe.ui.theme.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import com.example.airecipe.ui.theme.screens.homeScreenComponents.RecipeCard
import com.example.airecipe.ui.theme.screens.homeScreenComponents.RecipeGrid
import com.example.airecipe.ui.theme.screens.homeScreenComponents.SearchBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


// Home, Favorites, and Profile.

object HomeScreenDestination : AppNavigator {
    override val title = "Home"
    override val route = "home"
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRecipeClick: (Recipe) -> Unit
) {
    val allRecipes by viewModel.allRecipes.collectAsState()
    val searchedRecipes by viewModel.searchedRecipes.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllRecipes()  // Initial load
    }


    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            searchQuery.value = ""
            viewModel.clearSearchResults()
            val random = (0..100).random()
            viewModel.fetchAllRecipes(random)
        },
        modifier = Modifier.padding(top = 32.dp),
    ) {

        Log.d("HomeScreen", "All Recipes Count: ${allRecipes.size}")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            SearchBar(
                query = searchQuery.value,
                onQueryChange = { newQuery ->
                    searchQuery.value = newQuery
                    viewModel.searchRecipes(newQuery)
                },
                onSearch = { viewModel.searchRecipes(searchQuery.value) },
                onVoiceSearch = {},
                onClear = {
                    searchQuery.value = ""
                    viewModel.fetchAllRecipes()
                }
            )

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
                if (searchedRecipes.isNotEmpty()) {
                    Text(
                        text = "Search Results",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(start = 5.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(8.dp), // Ensure outer padding
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Equal vertical spacing
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Equal horizontal spacing
                    ) {
                        items(searchedRecipes) { recipe ->
                            RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe) })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (searchQuery.value.isNotEmpty()) {
                    NoResultsFound()
                }

                Text(
                    text = "All Recipes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.padding(start = 5.dp)
                )

                if (allRecipes.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp), // Ensure outer padding
                        verticalArrangement = Arrangement.spacedBy(8.dp), // Equal vertical spacing
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Equal horizontal spacing
                    ) {
                        items(allRecipes) { recipe ->
                            RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe) })
                        }
                    }
                } else {
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
            }
        }
    }
}



@Composable
fun NoResultsFound() {
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
        Text(
            text = "Try searching for 'Pasta' or 'Chicken'!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}