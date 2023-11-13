package com.eflc.mintdrop.ui.category

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.google.gson.Gson

@Composable
fun CategoryScreen(
    navComposable: NavController,
    category: ExpenseCategory
) {
    // val categoryViewModel: CategoryViewModel = hiltViewModel()
    val subCategories: List<ExpenseSubCategory> = category.subCategories.subList(1, category.subCategories.lastIndex)

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier
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

        subCategories.forEach { subcategory: ExpenseSubCategory ->
            ExpenseSubCategoryCard(
                subCategory = subcategory,
                onClick = {
                    val subcategoryJson = Uri.encode(Gson().toJson(it))
                    navComposable.navigate(route = AppScreens.ExpenseEntryScreen.route + "/$subcategoryJson")
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
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick(subCategory) }
    ) {
        Box(modifier = Modifier
            .width(400.dp)
            .height(36.dp)
        ) {
            Text(
                text = subCategory.subCategoryName,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}