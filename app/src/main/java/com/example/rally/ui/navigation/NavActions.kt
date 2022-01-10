package com.example.rally.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavActions(navController: NavHostController) {

    val navigateToRouteWithId: (String, Int) -> Unit = { route, id ->
        navController.navigate("$route/$id")
    }
    val navigateToRouteWithIdPop: (String, Int) -> Unit = { route, id ->
        navController.navigate("$route/$id") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToRoute: (String) -> Unit = { route ->
        navController.navigate(route)
    }
    val navigateToRoutePop: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}