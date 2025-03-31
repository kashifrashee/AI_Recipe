package com.example.airecipe.ui.theme.screens.homeScreenComponents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airecipe.data.repository.Recipe
import com.example.airecipe.data.repository.RecipeDetailResponse
import com.example.airecipe.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val apiKey1 =
        "fff438cc878b410981031ca152036841" // 🔹 Store API key here (Replace with actual key)
    // https://api.spoonacular.com/recipes/random?apiKey=fff438cc878b410981031ca152036841&number=10


    private val apiKey2 = "fff438cc878b410981031ca152036841"

    private val _featuredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val featuredRecipes: StateFlow<List<Recipe>> = _featuredRecipes.asStateFlow()

    private val _searchedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val searchedRecipes: StateFlow<List<Recipe>> = _searchedRecipes.asStateFlow()

    private val _recipeDetails = MutableStateFlow<RecipeDetailResponse?>(null)
    val recipeDetails: StateFlow<RecipeDetailResponse?> = _recipeDetails.asStateFlow()

    private val _similarRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val similarRecipes: StateFlow<List<Recipe>> = _similarRecipes.asStateFlow()

    private val _recentlyViewedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recentlyViewedRecipes: StateFlow<List<Recipe>> = _recentlyViewedRecipes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes: StateFlow<List<Recipe>> = _allRecipes

    private var currentOffset = 0  // Keeps track of pagination
    private val pageSize = 10      // Number of recipes per request

    init {
        fetchAllRecipes()
        fetchFeaturedRecipes()
    }


    // 🔹 Fetch Featured Recipes
    fun fetchFeaturedRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getFeaturedRecipes(apiKey1)
                Log.d("HomeViewModel", "Full featured API Response: ${response.recipes}")

                if (response.recipes.isNotEmpty()) { // ✅ Now correctly mapping to "recipes"
                    _featuredRecipes.value = response.recipes
                    Log.d("HomeViewModel", "Updated Featured Recipes: ${_featuredRecipes.value}") // ✅ Confirm update
                } else {
                    Log.e("HomeViewModel", " API returned empty results! Check quota or API key.")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching recipes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    // 🔹 Search Recipes
    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.searchRecipes(query, apiKey1)
                Log.d("HomeViewModel", "Full SEARCH API Response: $response")

                if (response.results.isNotEmpty()) {
                    _searchedRecipes.value =
                        response.results // ✅ Fix: Extract the list from RecipeResponse
                } else {
                    Log.e("HomeViewModel", "API returned empty results! Check quota or API key.")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error searching recipes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 🔹 Fetch all recipes
    fun fetchAllRecipes(offset: Int = 0) {

        _isLoading.value = true
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                val response = repository.fetchAllRecipes(apiKey1, offset)
                Log.d("HomeViewModel", "Full ALL API Response: ${response.number}")

                if (response.results.isNotEmpty()) {
                    _allRecipes.value = response.results // Ensure it updates correctly
                    Log.d("HomeViewModel", "Updated Recipes: ${_allRecipes.value}")
                } else {
                    Log.e("HomeViewModel", "API returned empty results! Check quota or API key.")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching all recipes: ${e.message}")
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    // 🔹 Fetch Recipe Details
    fun fetchRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            try {
                _recipeDetails.value = repository.getRecipeDetails(recipeId, apiKey1)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching recipe details: ${e.message}")
            }
        }
    }

    // 🔹 Fetch Similar Recipes
    fun fetchSimilarRecipes(recipeId: Int) {
        viewModelScope.launch {
            try {
                _similarRecipes.value = repository.getSimilarRecipes(recipeId, apiKey1)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching similar recipes: ${e.message}")
            }
        }
    }

    // 🔹 Fetch Recently Viewed Recipes from Firestore
    fun fetchRecentlyViewedRecipes(userId: String) {
        viewModelScope.launch {
            try {
                _recentlyViewedRecipes.value = repository.getRecentlyViewedRecipes(userId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching recently viewed recipes: ${e.message}")
            }
        }
    }

    // 🔹 Save Recently Viewed Recipe to Firestore
    fun saveRecentlyViewedRecipe(userId: String, recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.saveRecentlyViewedRecipe(userId, recipe)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error saving recently viewed recipe: ${e.message}")
            }
        }
    }

    fun clearSearchResults() {
        _searchedRecipes.value = emptyList() // Clear searched results
    }

}


