package com.eflc.mintdrop.screens.home

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eflc.mintdrop.graph.HomeNavigationGraph
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.utils.Constants

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        BottomNavigationItem(Constants.SETTINGS_SHEET_NAME, AppScreens.ExpenseScreen.route, Icons.Filled.Settings, Icons.Outlined.Settings),
        BottomNavigationItem(Constants.EXPENSE_SHEET_NAME, AppScreens.ExpenseScreen.route, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        BottomNavigationItem(Constants.INCOME_SHEET_NAME, AppScreens.IncomeScreen.route, Icons.Filled.AddCircle, Icons.Outlined.AddCircle)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = items,
                navController = navController
            )
        }
    ) {
        HomeNavigationGraph(navController = navController)
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    navController: NavHostController
) {
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navController.navigate(item.route)
                },
                label = { Text(text = item.title) },
                icon = {
                    Icon(
                        imageVector =
                        if (index == selectedItemIndex) {
                            item.selectedIcon
                        } else item.unselectedIcon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}