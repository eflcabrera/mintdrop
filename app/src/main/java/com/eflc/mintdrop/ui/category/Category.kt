package com.eflc.mintdrop.ui.category

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.card.CategoryCard
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navComposable: NavController,
    category: ExpenseCategory,
    sheet: String
) {
    val subCategories: List<ExpenseSubCategory> = category.subCategories

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = category.name) })
        }
    ) {
        if (subCategories.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 120.dp, bottom = 140.dp)
        ) {
            items(subCategories) { subcategory: ExpenseSubCategory ->
                ExpenseSubCategoryCard(
                    modifier = Modifier,
                    subCategory = subcategory,
                    onClick = {
                        val subcategoryJson = Uri.encode(Gson().toJson(it))
                        navComposable.navigate(route = AppScreens.ExpenseEntryScreen.route + "/$subcategoryJson/$sheet")
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseSubCategoryCard(
    modifier: Modifier,
    subCategory: ExpenseSubCategory,
    fontSize: TextUnit = 16.sp,
    onClick: (category: ExpenseSubCategory) -> Unit
) {
    val iconMap = mapOf("none" to 1)

    CategoryCard(iconMap = iconMap, category = subCategory, onClick = { onClick(subCategory) }, modifier = modifier, fontSize = fontSize)
}