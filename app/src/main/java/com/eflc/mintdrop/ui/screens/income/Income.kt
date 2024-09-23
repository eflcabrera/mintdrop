package com.eflc.mintdrop.ui.screens.income

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.components.card.CategoryCard
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.FormatUtils
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(navComposable: NavController) {
    val incomeViewModel: IncomeViewModel = hiltViewModel()

    LaunchedEffect(key1 = true, block = {
        incomeViewModel.getIncomeCategories()
        incomeViewModel.getMonthlyBalance()
    })

    val categories by incomeViewModel.incomeCategoryList.collectAsState()
    val monthlyBalance by incomeViewModel.monthlyBalance.collectAsState()
    val sheet = Constants.INCOME_SHEET_NAME

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 120.dp, bottom = 140.dp)
        ) {
            items(categories) { category: ExpenseCategory ->
                ExpenseCategoryCard(
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
    category: ExpenseCategory,
    onClick: (category: ExpenseCategory) -> Unit
) {
    val iconMap = mapOf("none" to 1)
    CategoryCard(iconMap = iconMap, category = category, onClick = { onClick(category) }, modifier = Modifier)
}
