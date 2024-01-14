package com.eflc.mintdrop.ui.home

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
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.card.CategoryCard
import com.eflc.mintdrop.utils.Constants
import com.google.gson.Gson

@Composable
fun HomeScreen(navComposable: NavController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val categories by homeViewModel.state.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(400.dp)
            .height(36.dp)
    ) {
        Text(
            text = Constants.EXPENSE_SHEET_NAME,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(alignment = Alignment.Center)
        )
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
            .fillMaxSize()
            .padding(top = 50.dp),
        columns = GridCells.Adaptive(250.dp),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(categories) { category: ExpenseCategory ->
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
    val iconMap = mapOf(
        "Deuda" to R.drawable.baseline_debt_24,
        "Educación" to R.drawable.baseline_school_24,
        "Ocio" to R.drawable.baseline_leisure_24,
        "Gastos diarios" to R.drawable.baseline_payments_24,
        "Regalos" to R.drawable.baseline_card_giftcard_24,
        "Salud/médicos" to R.drawable.baseline_medication_24,
        "Vivienda" to R.drawable.baseline_home_24,
        "Seguros" to R.drawable.baseline_security_24,
        "Mascotas" to R.drawable.baseline_pets_24,
        "Tecnología" to R.drawable.baseline_hardware_24,
        "Transporte" to R.drawable.baseline_transport_24,
        "Viajes" to R.drawable.baseline_travel_24,
        "Servicios básicos" to R.drawable.baseline_services_24,
        "Ahorro" to R.drawable.baseline_savings_24,
        "Impuestos" to R.drawable.baseline_taxes_24
    )
    CategoryCard(iconMap = iconMap, category = category, onClick = { onClick(category) })
}
