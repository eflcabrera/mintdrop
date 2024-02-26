package com.eflc.mintdrop.ui.category

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.card.CategoryCard
import com.google.gson.Gson

@Composable
fun CategoryScreen(
    navComposable: NavController,
    category: ExpenseCategory,
    sheet: String
) {
    val subCategories: List<ExpenseSubCategory> = category.subCategories.subList(1, category.subCategories.lastIndex)

    Box(
        modifier = Modifier
            .width(400.dp)
            .height(36.dp)
    ) {
        Text(
            text = category.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)
                .align(alignment = Alignment.Center)
        )
    }

    if (subCategories.isEmpty()) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(align = Alignment.Center)
        )
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(subCategories) { subcategory: ExpenseSubCategory ->
            ExpenseSubCategoryCard(
                subCategory = subcategory,
                onClick = {
                    val subcategoryJson = Uri.encode(Gson().toJson(it))
                    navComposable.navigate(route = AppScreens.ExpenseEntryScreen.route + "/$subcategoryJson/$sheet")
                }
            )
        }
    }
}

@Composable
fun ExpenseSubCategoryCard(
    subCategory: ExpenseSubCategory,
    onClick: (category: ExpenseSubCategory) -> Unit
) {
    val iconMap = mapOf("none" to 1)

    CategoryCard(iconMap = iconMap, category = subCategory, onClick = { onClick(subCategory) }, modifier = Modifier, fontSize = 16.sp)
}