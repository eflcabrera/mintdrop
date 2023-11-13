package com.eflc.mintdrop.ui.home

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eflc.mintdrop.models.ExpenseCategory
import com.eflc.mintdrop.navigation.AppScreens
import com.google.gson.Gson

@Composable
fun HomeScreen(navComposable: NavController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val state by homeViewModel.state.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
            )
        }

        state.forEach { category: ExpenseCategory ->
            ExpenseCategoryCard(
                category = category,
                onClick = {
                    val categoryJson = Uri.encode(Gson().toJson(it))
                    navComposable.navigate(route = AppScreens.CategoryScreen.route + "/$categoryJson")
                }
            )
        }
    }
}

@Composable
fun ExpenseCategoryCard(
    category: ExpenseCategory,
    onClick: (category: ExpenseCategory) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick(category) },
    ) {
        Box(modifier = Modifier
            .width(400.dp)
            .height(36.dp)
        ) {
            Text(
                text = category.name,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(alignment = Alignment.Center)
            )
        }
    }
}
