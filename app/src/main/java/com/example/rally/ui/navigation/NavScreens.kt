package com.example.rally.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rally.R

sealed class Screen(val route: String) {
    object HomeScreens          : Screen("home")
    object SavingsScreens       : Screen("savings")
    object TransactionsScreens  : Screen("transactions")
    object SearchScreen         : Screen("search")
}

val bottomNavItems = listOf<Sections>(
    HomeSections.Main,
    SavingsSections.Main,
    TransactionsSections.Main,
    SearchSections.Main
)


sealed class HomeSections : Sections {
    object Main : HomeSections() {
        override val route: String get() = "home/main"
        override val resId: Int get() = R.string.main_sections_home
        override val icon: ImageVector get() = Icons.Filled.PieChart
    }
}
sealed class SavingsSections(
    override val route: String,
    override val resId: Int,
    override val icon: ImageVector
) : Sections {
    object Main     : SavingsSections("savings/main", R.string.main_sections_savings, Icons.Filled.TrendingUp)
    object Create   : SavingsSections("savings/create", R.string.main_sections_savings, Icons.Filled.TrendingUp)
    object Edit     : SavingsSections("savings/edit", R.string.main_sections_savings, Icons.Filled.TrendingUp)
}

sealed class TransactionsSections(
    override val route: String,
    override val resId: Int,
    override val icon: ImageVector
) : Sections {
    object Main : TransactionsSections("transactions/main", R.string.main_sections_transactions, Icons.Filled.Payments)
}

sealed class SearchSections(
    override val route: String,
    override val resId: Int,
    override val icon: ImageVector
) : Sections {
    object Main : SearchSections("search/main", R.string.main_sections_search, Icons.Filled.Search)
}

sealed interface Sections {

    val route: String
    val resId: Int
    val icon: ImageVector

//    open class HomeSections(
//        override val route: String,
//        override val resId: Int,
//        override val icon: ImageVector
//    ) : Sections {
//        object Main : HomeSections("home/main", R.string.main_sections_home, Icons.Filled.PieChart)
//    }
//
//    open class SavingsSections(
//        override val route: String,
//        override val resId: Int,
//        override val icon: ImageVector
//    ) : Sections {
//        object Main     : SavingsSections("savings/main", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//        object Create   : SavingsSections("savings/create", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//        object Edit     : SavingsSections("savings/edit", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//    }
//
//    open class TransactionsSections(
//        override val route: String,
//        override val resId: Int,
//        override val icon: ImageVector
//    ) : Sections {
//        object Main : TransactionsSections("transactions/main", R.string.main_sections_transactions, Icons.Filled.Payments)
//    }


}


//sealed class HomeSections(
//    val route: String,
//    @StringRes val resId: Int,
//    val icon: ImageVector
//) {
//    object Main : HomeSections("home/main", R.string.main_sections_home, Icons.Filled.PieChart)
//}
//
//sealed class SavingsSections(
//    val route: String,
//    @StringRes val resId: Int,
//    val icon: ImageVector
//) {
//    object Main     : SavingsSections("savings/main", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//    object Create   : SavingsSections("savings/create", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//    object Edit     : SavingsSections("savings/edit", R.string.main_sections_savings, Icons.Filled.TrendingUp)
//}
//
//sealed class TransactionsSections(
//    val route: String,
//    @StringRes val resId: Int,
//    val icon: ImageVector
//) {
//    object Main : TransactionsSections("transactions/main", R.string.main_sections_transactions, Icons.Filled.Payments)
//}

