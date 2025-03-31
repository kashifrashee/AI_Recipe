package com.example.airecipe.model

import com.example.airecipe.data.repository.FeaturedRecipeResponse
import com.example.airecipe.data.repository.Recipe
import com.example.airecipe.data.repository.RecipeDetailResponse
import com.example.airecipe.data.repository.RecipeResponse
import com.example.airecipe.data.repository.SearchRecipeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface SpoonacularApiService {

    @GET("recipes/random") // Example endpoint for featured recipes
    suspend fun getFeaturedRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 5
    ): FeaturedRecipeResponse

    @GET("recipes/complexSearch")
    suspend fun getAllRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 20,  // Number of recipes per page
        @Query("offset") offset: Int = 0   // Offset for pagination
    ): RecipeResponse

    @GET("recipes/complexSearch") // Endpoint for searching recipes
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String
    ): SearchRecipeResponse

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): RecipeDetailResponse

    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 5 // Default to 5 similar recipes
    ): List<Recipe>
}
