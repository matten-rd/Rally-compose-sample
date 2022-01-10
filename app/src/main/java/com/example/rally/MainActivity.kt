package com.example.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rally.ui.navigation.NavActions
import com.example.rally.ui.navigation.RallyNavGraph
import com.example.rally.ui.navigation.RallyTopAppBarHandler
import com.example.rally.ui.theme.RallyTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    Color.Transparent,
                    darkIcons = useDarkIcons,
                    false
                )
            }

            RallyTheme {
                ProvideWindowInsets(
                    windowInsetsAnimationsEnabled = true,
                    consumeWindowInsets = false
                ) {
                    RallyApp()
                }

            }
        }
    }
}


@Composable
fun RallyApp() {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navActions = remember(navController) { NavActions(navController) }
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            RallyTopAppBarHandler(
                currentRoute = currentDestination?.route,
                navActions = navActions,
                navController = navController
            )
        }
    ) { innerPadding ->
        RallyNavGraph(
            contentPadding = innerPadding,
            navController = navController,
            scaffoldState = scaffoldState
        )
    }
}

