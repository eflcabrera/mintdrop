package com.eflc.mintdrop.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.eflc.mintdrop.R
import com.eflc.mintdrop.graph.HomeNavigationGraph
import com.eflc.mintdrop.navigation.AppScreens
import com.eflc.mintdrop.ui.components.dialog.UndoEntryDialog
import com.eflc.mintdrop.utils.Constants
import com.eflc.mintdrop.utils.FormatUtils.Companion.formatAsCurrency

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
    val homeViewModel: HomeViewModel = hiltViewModel()
    val items = listOf(
        BottomNavigationItem(Constants.EXPENSE_SHEET_NAME, AppScreens.ExpenseScreen.route, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        BottomNavigationItem(Constants.SHARED_EXPENSE_SHEET_NAME, AppScreens.SharedExpensesScreen.route, Icons.Filled.Favorite, Icons.Outlined.Favorite),
        BottomNavigationItem(Constants.INCOME_SHEET_NAME, AppScreens.IncomeScreen.route, Icons.Filled.AddCircle, Icons.Outlined.AddCircle)
    )

    val lastEntry by homeViewModel.lastEntryData.collectAsState()

    val shouldShowUndoDialog = remember { mutableStateOf(false) }

    if (shouldShowUndoDialog.value) {
        UndoEntryDialog(
            onDismissRequest = { },
            onConfirmation = { homeViewModel.deleteEntry() },
            dialogTitle = "Deshacer último",
            dialogText = "¿Revertir gasto \"${lastEntry.description}\" en \"${lastEntry.categoryName}\" por $${formatAsCurrency(lastEntry.amount)}?",
            isVisible = shouldShowUndoDialog
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        homeViewModel.getLastEntry()
                        shouldShowUndoDialog.value = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.undo_left_svgrepo_com),
                        "Undo last entry button"
                    )
                }
            }
        },
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
