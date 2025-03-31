package com.eflc.mintdrop.ui.screens.expense

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.R
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.models.ExpenseSubCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.components.card.CategoryCard
import com.eflc.mintdrop.ui.screens.category.ExpenseSubCategoryCard
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.FormatUtils
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(navComposable: NavController) {
    val expenseViewModel: ExpenseViewModel = hiltViewModel()
    
    LaunchedEffect(key1 = true, block = {
        expenseViewModel.getExpenseCategories()
        expenseViewModel.getRecentlyUsedSubcategories()
        expenseViewModel.getMonthlyBalance()
    })
    
    val categories by expenseViewModel.expenseCategoryList.collectAsState()
    val lastUsedSubcategories by expenseViewModel.recentlyUsedList.collectAsState()
    val monthlyBalance by expenseViewModel.monthlyBalance.collectAsState()
    val sheet = Constants.EXPENSE_SHEET_NAME

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
            ) {
                CenterAlignedTopAppBar(title = { Text(text = sheet) })
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, end = 40.dp, top = 70.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total del mes: ${FormatUtils.formatAsCurrency(monthlyBalance)}",
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.Top,
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 120.dp
                    )
                ) {
                    items(lastUsedSubcategories) { subCategory: ExpenseSubCategory ->
                        ExpenseSubCategoryCard(
                            modifier = Modifier.height(80.dp),
                            subCategory = subCategory,
                            onClick = {
                                val subcategoryJson = Uri.encode(Gson().toJson(it))
                                navComposable.navigate(route = AppScreens.ExpenseEntryScreen.route + "/$subcategoryJson/$sheet")
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(400.dp)
                .height(36.dp)
        ) {
            if (categories.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                        .padding(top = 100.dp)
                )
            }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            columns = GridCells.Adaptive(140.dp),
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 230.dp, bottom = 140.dp)
        ) {
            items(categories) { category: ExpenseCategory ->
                ExpenseCategoryCard(
                    modifier = Modifier,
                    category = category,
                    onClick = {
                        val categoryJson = Uri.encode(Gson().toJson(it))
                        navComposable.navigate(route = AppScreens.CategoryScreen.route + "/$categoryJson/$sheet")
                    }
                )
            }
        }
    }
}

@Composable
fun ExpenseCategoryCard(
    modifier: Modifier,
    category: ExpenseCategory,
    onClick: (category: ExpenseCategory) -> Unit
) {
    val iconMap = mapOf(
        "Deuda" to R.drawable.apartment,
        "Educación" to R.drawable.graduation_hat,
        "Ocio" to R.drawable.dice,
        "Gastos diarios" to R.drawable.cart,
        "Regalos" to R.drawable.gift,
        "Salud/médicos" to R.drawable.heart_pulse,
        "Vivienda" to R.drawable.home,
        "Seguros" to R.drawable.lock,
        "Mascotas" to R.drawable.paw,
        "Tecnología" to R.drawable.laptop_phone,
        "Transporte" to R.drawable.car,
        "Viajes" to R.drawable.map,
        "Servicios básicos" to R.drawable.inbox,
        "Ahorro o Inversión" to R.drawable.star,
        "Impuestos" to R.drawable.poop
    )
    CategoryCard(iconMap = iconMap, category = category, onClick = { onClick(category) }, modifier = modifier)
}
