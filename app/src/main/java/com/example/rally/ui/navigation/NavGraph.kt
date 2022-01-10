package com.example.rally.ui.navigation


import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.*
import com.example.rally.ui.home.HomeScreen
import com.example.rally.ui.savings.SavingsCreateScreen
import com.example.rally.ui.savings.SavingsDetailScreen
import com.example.rally.ui.savings.SavingsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation

const val ACCOUNT_ID_KEY = "accountId"


@Composable
fun RallyNavGraph(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    startDestination: String = Screen.HomeScreens.route
) {
    val navActions = remember(navController) { NavActions(navController) }
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.padding(contentPadding)
    ) {
        mainGraph(navActions, scaffoldState)
    }
}


fun NavGraphBuilder.mainGraph(
    actions: NavActions,
    scaffoldState: ScaffoldState
) {
    navigation(
        startDestination = HomeSections.Main.route,
        route = Screen.HomeScreens.route,
        enterTransition = { rallyEnterTransition() },
        exitTransition = { rallyExitTransition() }
    ) {

        composable(HomeSections.Main.route) {
            HomeScreen(
                navigateToSavingsScreen = { actions.navigateToRoutePop(SavingsSections.Main.route) },
                navigateToEditScreen = { id -> actions.navigateToRouteWithId(SavingsSections.Edit.route, id) }
            )
        }


        savingsGraph(actions, scaffoldState)
        transactionsGraph()
        searchGraph()

    }
}


fun NavGraphBuilder.savingsGraph(
    actions: NavActions,
    scaffoldState: ScaffoldState
) {
    navigation(
        startDestination = SavingsSections.Main.route,
        route = Screen.SavingsScreens.route,
        enterTransition = { rallyEnterTransition() },
        exitTransition = { rallyExitTransition() }
    ) {
        composable(route = SavingsSections.Main.route) {
            SavingsScreen(
                scaffoldState = scaffoldState,
                navigateToCreateScreen = { actions.navigateToRoute(SavingsSections.Create.route) },
                navigateToEditScreen = { id -> actions.navigateToRouteWithId(SavingsSections.Edit.route, id) },
            )
        }
        composable(SavingsSections.Create.route) {
            Column(Modifier.fillMaxSize()) {
                SavingsCreateScreen(onNavigateBack = actions.upPress)
            }
        }
        composable(
            "${SavingsSections.Edit.route}/{$ACCOUNT_ID_KEY}",
            arguments = listOf(navArgument(ACCOUNT_ID_KEY) { type = NavType.IntType })
        ) {
            // the accountId is retrieved from the savedStateHandle in the viewModel
            SavingsDetailScreen(onNavigateBack = actions.upPress, scaffoldState = scaffoldState)
        }
    }
}

fun NavGraphBuilder.transactionsGraph(

) {
    navigation(
        startDestination = TransactionsSections.Main.route,
        route = Screen.TransactionsScreens.route,
        enterTransition = { rallyEnterTransition() },
        exitTransition = { rallyExitTransition() }
    ) {
        composable(TransactionsSections.Main.route) {
            Column(Modifier.fillMaxSize().background(Color.Green)) {

            }
        }

    }
}

fun NavGraphBuilder.searchGraph(

) {
    navigation(
        startDestination = SearchSections.Main.route,
        route = Screen.SearchScreen.route,
        enterTransition = { rallyEnterTransition() },
        exitTransition = { rallyExitTransition() }
    ) {
        composable(SearchSections.Main.route) {
            Column(Modifier.fillMaxSize().background(Color.Red)) {
                /**

                    "Search and filter screen should look similar to Google messages search" +
                            "Have filters for like large transactions, small transactions etc" +
                            "Filter for month year day" +
                            "Filter for category" +
                            "Filter for savings account" +
                            "And just plain search"

                */
            }
        }
    }
}


fun AnimatedContentScope<NavBackStackEntry>.rallyEnterTransition(): EnterTransition {
    return if (bottomNavItems.any { it.route == targetState.destination.route } &&
        bottomNavItems.any { it.route == initialState.destination.route }) {
        rallyPeerEnterTransition()
    } else {
        rallyParentChildEnterTransition()
    }
}

fun AnimatedContentScope<NavBackStackEntry>.rallyExitTransition(): ExitTransition {
    return if (bottomNavItems.any { it.route == targetState.destination.route } &&
        bottomNavItems.any { it.route == initialState.destination.route }) {
        rallyPeerExitTransition()
    } else {
        rallyParentChildExitTransition()
    }
}


fun AnimatedContentScope<NavBackStackEntry>.rallyPeerEnterTransition(): EnterTransition {
    val routes = bottomNavItems.map { it.route }
    val initialIndex = routes.indexOf(initialState.destination.route)
    val targetIndex = routes.indexOf(targetState.destination.route)

    return if (initialIndex < targetIndex) {
        slideInHorizontally(initialOffsetX = { it })
    } else {
        slideInHorizontally(initialOffsetX = { -it })
    }
}

fun AnimatedContentScope<NavBackStackEntry>.rallyPeerExitTransition(): ExitTransition {
    val routes = bottomNavItems.map { it.route }
    val initialIndex = routes.indexOf(initialState.destination.route)
    val targetIndex = routes.indexOf(targetState.destination.route)

    return if (initialIndex > targetIndex) {
        slideOutHorizontally(targetOffsetX = { it })
    } else {
        slideOutHorizontally(targetOffsetX = { -it })
    }
}

fun AnimatedContentScope<NavBackStackEntry>.rallyParentChildEnterTransition1(): EnterTransition {
    return if (bottomNavItems.any { it.route == targetState.destination.route }) {
        //slideInVertically(initialOffsetY = { -it })
        fadeIn()
    } else {
        //slideInVertically(initialOffsetY = { it })
        expandVertically(
            expandFrom = Alignment.CenterVertically,
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        )
    }
}

fun AnimatedContentScope<NavBackStackEntry>.rallyParentChildExitTransition1(): ExitTransition {
    return if (bottomNavItems.any { it.route == initialState.destination.route }) {
        //slideOutVertically(targetOffsetY = { -it })
        fadeOut()
    } else {
        //slideOutVertically(targetOffsetY = { it })
        shrinkVertically(
            shrinkTowards = Alignment.CenterVertically,
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutLinearInEasing
            )
        )
    }
}

fun AnimatedContentScope<NavBackStackEntry>.rallyParentChildEnterTransition(
    durationMillis: Int = DurationMillis,
): EnterTransition {
    val initialScale = if (bottomNavItems.any { it.route == targetState.destination.route }) {
        // If navigating TO a Main screen FROM a child destination
        1.1f
    } else {
        0.8f
    }

    return fadeIn(
            animationSpec = tween(
                durationMillis = durationMillis.ForIncoming,
                delayMillis = durationMillis.ForOutgoing,
                easing = LinearOutSlowInEasing
            )
        ) + scaleIn(
            initialScale = initialScale,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            )
        )

}

fun AnimatedContentScope<NavBackStackEntry>.rallyParentChildExitTransition(
    durationMillis: Int = DurationMillis
): ExitTransition {
    val targetScale = if (bottomNavItems.any { it.route == targetState.destination.route }) {
        // If navigating FROM a Main screen TO a child destination
        0.8f
    } else {
        1.1f
    }

    return fadeOut(
        animationSpec = tween(
            durationMillis = durationMillis.ForOutgoing,
            delayMillis = 0,
            easing = FastOutLinearInEasing
        )
    ) + scaleOut(
        targetScale = targetScale,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = FastOutSlowInEasing
        )
    )
}

private const val DurationMillis = 300
private const val ProgressThreshold = 0.35f
// 35% of specified value
private val Int.ForOutgoing: Int
    get() = (this * ProgressThreshold).toInt()
// 65% of specified value
private val Int.ForIncoming: Int
    get() = this - this.ForOutgoing
