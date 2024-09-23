package com.eflc.mintdrop.ui.screens.category

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.models.EntryType
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.components.card.CategoryCard
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.FormatUtils
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navComposable: NavController,
    category: ExpenseCategory,
    sheet: String
) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()
    val subCategories: List<ExpenseSubCategory> = category.subCategories
    val isExpense = sheet == Constants.EXPENSE_SHEET_NAME
    val categoryType = if (isExpense) EntryType.EXPENSE else EntryType.INCOME

    LaunchedEffect(key1 = true, block = {
        categoryViewModel.getMonthlyBalance(category.id, categoryType)
    })

    val monthlyBalance by categoryViewModel.monthlyBalance.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
            ) {
                CenterAlignedTopAppBar(title = { Text(text = category.name) })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, top = 100.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total del mes: $ ${FormatUtils.formatAsCurrency(monthlyBalance)}",
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        },
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 150.dp, bottom = 140.dp)
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
