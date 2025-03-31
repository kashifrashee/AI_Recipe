package com.example.airecipe.data.repository

import com.google.gson.annotations.SerializedName

data class Recipe(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val readyInMinutes: Int = 0,  // Cooking time
    val servings: Int = 1,
    val sourceUrl: String = "", // Link to the full recipe
    val summary: String = "",
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class RecipeResponse(
    @SerializedName("results") val results: List<Recipe> = emptyList(), // ✅ Correct mapping
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("number") val number: Int = 10,
    @SerializedName("totalResults") val totalResults: Int = 0
)

// This class will be used in searchRecipes() and getFeaturedRecipes()
data class FeaturedRecipeResponse(
    @SerializedName("recipes") val recipes: List<Recipe> = emptyList() // ✅ Used for featured recipes
)

data class SearchRecipeResponse(
    @SerializedName("results") val results: List<Recipe> = emptyList() // ✅ Used for search results
)


// This will be used in getRecipeDetails() API
data class RecipeDetailResponse(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val readyInMinutes: Int = 0,
    val servings: Int = 1,
    val sourceUrl: String = "",
    val summary: String = "",
    val extendedIngredients: List<Ingredient> = emptyList(),
    val instructions: String = ""
)

data class Ingredient(
    val id: Int = 0,
    val name: String = "",
    val amount: Double = 0.0,
    val unit: String = ""
)
