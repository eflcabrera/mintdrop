package com.eflc.mintdrop.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eflc.mintdrop.navigation.Graphs
import com.eflc.mintdrop.screens.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graphs.ROOT,
        startDestination = Graphs.HOME
    ) {
        composable(route = Graphs.HOME) {
            HomeScreen()
        }
    }
}