package com.example.airecipe.ui.theme.screens.homeScreenComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.airecipe.R
import com.example.airecipe.data.repository.Recipe
import com.example.airecipe.ui.theme.ButtonColor

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,  // Added onClear parameter
    onVoiceSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f),
            placeholder = { Text(text = "Search Recipes...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
        )
        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = ButtonColor.copy(0.5f)),
            modifier = Modifier
                .size(48.dp)
                .clickable { onVoiceSearch() },
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_mic_24),
                contentDescription = "Voice Search",
                modifier = Modifier.padding(12.dp),
                tint = Color.Black
            )
        }
    }
}



@Composable
fun CategoriesRow(
    modifier: Modifier = Modifier,
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    if (categories.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        labelColor = Color.White,
                        selectedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    } else {
        Text(
            text = "No Categories are available"
        )
    }

}


@Composable
fun FeaturedRecipes(
    modifier: Modifier = Modifier,
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit
) {
    if (recipes.isNotEmpty()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe) })
            }
        }
    } else {
        Text(
            text = "No Featured Recipes are available..."
        )
    }

}

@Composable
fun RecipeGrid(
    modifier: Modifier = Modifier,
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit
) {
    if (recipes.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe) }
                )
            }
        }
    } else {
        Text(
            text = "No Recipes are available.."
        )
    }

}

@Composable
fun RecentlyViewedRecipes(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit
) {
    if (recipes.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Recently Viewed",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recipes) { recipe ->
                    RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe) })
                }
            }
        }
    } else {
        Text(
            text = "You have not viewed any recipes"
        )
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 180.dp, height = 200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/*@Preview
@Composable
private fun RecipeGridPrev() {
    val recipes = listOf(
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        ),
        Recipe(
            title = "Recipe 1",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBVrjECl0O8kmhwY2gReCcjRooKpgAR7Kc5d35h5pGteUvA0YZo5bS0-bHra8O1sNh1ZU&usqp=CAU"
        )
    )
    RecipeGrid(
        recipes = recipes,
        onRecipeClick = {

        }
    )
}*/

/*
@Preview
@Composable
private fun SearchBarPrev() {
    SearchBar(
        query = "",
        onQueryChange = {},
        onSearch = {},
        onVoiceSearch = {}
    )
}*/


/*
@Preview
@Composable
private fun CategoriesRowPreview() {
    val categories = listOf(
        "Diet Food", "Healthy Food", "Crispy Food"
    )
    CategoriesRow(
        categories = categories,
        selectedCategory = null,
        onCategorySelected = {}
    )
}*/
