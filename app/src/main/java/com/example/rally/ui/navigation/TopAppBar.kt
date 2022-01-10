package com.example.rally.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rally.R
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import java.util.*

@Composable
fun RallyTopAppBarHandler(
    currentRoute: String?,
    navActions: NavActions,
    navController: NavController
) {
    val resId = remember(currentRoute) { getResFromRoute(currentRoute) }
    val title = stringResource(id = resId)

    when(currentRoute) {
        in bottomNavItems.map { it.route } -> RallyTopBarNavigation(navController = navController)

        else -> NavigationTopAppBar(title = title, onBackPress = navActions.upPress)
    }
}

@Composable
fun NavigationTopAppBar(
    title: String,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
    RallyTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Navigate back")
            }
        },
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = elevation
    )
}

@Composable
fun RallyTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation
) {
   com.google.accompanist.insets.ui.TopAppBar(
       title = {
           Text(
               text = title.uppercase(Locale.getDefault()),
               style = MaterialTheme.typography.h6,
               fontWeight = FontWeight.Bold
           )
       },
       modifier = modifier,
       contentPadding = rememberInsetsPaddingValues(
           insets = LocalWindowInsets.current.statusBars, applyBottom = false
       ),
       navigationIcon = navigationIcon,
       actions = actions,
       backgroundColor = backgroundColor,
       contentColor = contentColor,
       elevation = elevation
   )
}


@Composable
fun RallyTopBarNavigation(
    navController: NavController,
    tabs: List<Sections> = bottomNavItems
) {
    val contentPadding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.statusBars,
        applyBottom = false,
        additionalStart = 8.dp
    )

    Surface(
        Modifier
            .padding(contentPadding)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .selectableGroup()
                .height(TabHeight),
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            tabs.forEach { screen ->
                RallyTab(
                    icon = screen.icon,
                    label = stringResource(id = screen.resId),
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onSelected = {
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

@Composable
private fun RallyTab(
    selected: Boolean,
    onSelected: () -> Unit,
    label: String,
    icon: ImageVector
) {
    val color = MaterialTheme.colors.onSurface
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) color else color.copy(InactiveTabOpacity),
        animationSpec = animSpec
    )
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .animateContentSize()
            .height(TabHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(42.dp, 42.dp)
                .clip(CircleShape)
                .selectable(
                    selected = selected,
                    onClick = onSelected,
                    role = Role.Tab,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tabTintColor,
            )
        }
        if (selected) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label.uppercase(Locale.getDefault()),
                color = tabTintColor,
                modifier = Modifier.clearAndSetSemantics {  },
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

private fun getResFromRoute(route: String?): Int {
    return when(route) {
        HomeSections.Main.route -> HomeSections.Main.resId
        SavingsSections.Main.route -> SavingsSections.Main.resId
        TransactionsSections.Main.route -> TransactionsSections.Main.resId

        else -> R.string.empty_string
    }
}