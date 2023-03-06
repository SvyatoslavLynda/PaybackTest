package com.svdroid.paybacktest.ui.screen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.svdroid.paybacktest.utils.Destination

@ExperimentalMaterialApi
@Composable
fun PixabayNavHost(navController: NavHostController, onVoiceInputClick: () -> Unit) {
    NavHost(navController = navController, startDestination = Destination.Main.route) {
        composable(
            route = Destination.Main.route,
            arguments = listOf(navArgument("searchQuery") {
                type = NavType.StringType
                nullable = true
            }),
        ) { navBackStackEntry ->
            val searchQuery = navBackStackEntry.arguments?.getString("searchQuery") ?: ""

            MainPage(searchQuery = searchQuery, onVoiceInputClick = onVoiceInputClick)
        }
        composable(
            route = Destination.Search.route,
            arguments = listOf(navArgument("searchQuery") {
                type = NavType.StringType
                nullable = true
            }),
        ) { navBackStackEntry ->
            val searchQuery = navBackStackEntry.arguments?.getString("searchQuery") ?: ""

            SearchPage(searchQuery = searchQuery)
        }
        composable(
            route = Destination.Details.route,
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            }),
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt("id") ?: return@composable

            DetailsPage(id = id)
        }
        dialog(
            route = Destination.ConfirmNavigationToDetails.route,
            arguments = listOf(navArgument("id") {
                type = NavType.IntType
            }),
            dialogProperties = DialogProperties()
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt("id") ?: return@dialog

            AppAlertDialog(hitId = id, navController = navController)
        }
    }
}