package com.example.airecipe.data.repository

import android.util.Log
import com.example.airecipe.model.SpoonacularApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecipeRepository @Inject constructor(private val apiService: SpoonacularApiService) {

    suspend fun getFeaturedRecipes(apiKey: String, number: Int = 8): FeaturedRecipeResponse {
        Log.d("RecipeRepository", "Fetching featured recipes with API key: $apiKey")
        return apiService.getFeaturedRecipes(apiKey, number)
    }

    suspend fun searchRecipes(query: String, apiKey: String): SearchRecipeResponse {
        Log.d("RecipeRepository", "Searching recipes with query: $query and API key: $apiKey")
        return apiService.searchRecipes(query, apiKey)
    }

    suspend fun fetchAllRecipes(apiKey: String, offset: Int = 0): RecipeResponse {
        Log.d("RecipeRepository", "Fetching recipes with offset: $offset")
        return apiService.getAllRecipes(apiKey, number = 20, offset = offset)
    }


    suspend fun getRecipeDetails(recipeId: Int, apiKey: String): RecipeDetailResponse {
        Log.d(
            "RecipeRepository",
            "Fetching recipe details for recipe ID: $recipeId with API key: $apiKey"
        )
        return apiService.getRecipeDetails(recipeId, apiKey)
    }


    suspend fun getSimilarRecipes(recipeId: Int, apiKey: String, number: Int = 5): List<Recipe> {
        return apiService.getSimilarRecipes(recipeId, apiKey, number)
    }

    private val firestore = FirebaseFirestore.getInstance()

    // 🔹 New: Get Recently Viewed Recipes from Firestore
    suspend fun getRecentlyViewedRecipes(userId: String): List<Recipe> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("recentlyViewed")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 🔹 New: Save Recently Viewed Recipe to Firestore
    suspend fun saveRecentlyViewedRecipe(userId: String, recipe: Recipe) {
        val recipeData = recipe.copy() // Create a copy to avoid modifying original
        val recipeRef = firestore.collection("users")
            .document(userId)
            .collection("recentlyViewed")
            .document(recipe.id.toString())

        recipeRef.set(
            recipeData.copy(
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
