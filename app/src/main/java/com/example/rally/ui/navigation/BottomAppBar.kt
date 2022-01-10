package com.example.rally.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun RallyBottomNavigation(navController: NavController, tabs: List<Sections> = bottomNavItems) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route in tabs.map { it.route }) {
        Column {
            com.google.accompanist.insets.ui.BottomNavigation(
                contentPadding = rememberInsetsPaddingValues(insets = LocalWindowInsets.current.navigationBars)
            ) {
                tabs.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                        label = { Text(text = stringResource(id = screen.resId)) },
                        alwaysShowLabel = false,
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}