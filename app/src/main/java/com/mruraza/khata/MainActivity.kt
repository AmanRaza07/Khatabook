package com.mruraza.khata

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mruraza.khata.presentation.UI.CustomerScreen
import com.mruraza.khata.presentation.UI.CustomerTransactions
import com.mruraza.khata.presentation.UI.ItemsScreen
import com.mruraza.khata.presentation.UI.SplashScreen
import com.mruraza.khata.presentation.UI.SyncScreen
import com.mruraza.khata.ui.theme.KhataTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KhataTheme {
                val navController = rememberNavController()
                val currentRoute = currentRoute(navController)
                val context = LocalContext.current
                Scaffold(
                    bottomBar = {
                        // Show bottom bar only for main screens, hide for splash screen
                        if (currentRoute != NavigationDestination.SPLASH_SCREEN) {
                            AppBottomBar(
                                currentRoute = currentRoute,
                                onSelect = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationDestination.SPLASH_SCREEN,
                    ) {

                        composable(NavigationDestination.CUSTOMER) {
                            BackHandler {
                                (context as Activity).finish() // close app
                            }
                            CustomerScreen(
                                navController,
                                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                                bottomPadding = innerPadding
                            )
                        }

                        composable(NavigationDestination.ITEMS) {
                            BackHandler {
                                navController.navigate(NavigationDestination.CUSTOMER) {
                                    popUpTo(NavigationDestination.CUSTOMER) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                            ItemsScreen(
                                navController,
                                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                                modifierFloatingAction = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                                bottomPadding = innerPadding
                            )
                        }

                        composable(NavigationDestination.SETTING) {
                            BackHandler {
                                navController.navigate(NavigationDestination.CUSTOMER) {
                                    popUpTo(NavigationDestination.CUSTOMER) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                            SyncScreen(navController)
                        }

                        composable(
                            "transactions/{customerId}",
                            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val customerId = backStackEntry.arguments!!.getInt("customerId")
                            CustomerTransactions(
                                navController,
                                customerId,
                                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                                activity = this@MainActivity,
                                bottomPadding = innerPadding
                            )
                        }

                        composable(NavigationDestination.SPLASH_SCREEN) {
                            SplashScreen(navController)
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun AppBottomBar(
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    NavigationBar {
        BottomNavItem.values().forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onSelect(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}


enum class BottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    CUSTOMERS("ग्राहकहरू", Icons.Default.Group, NavigationDestination.CUSTOMER),
    ITEMS("सामानहरू", Icons.Default.ShoppingCart, NavigationDestination.ITEMS),
    SETTING("सेटिङ", Icons.Default.Settings, NavigationDestination.SETTING)
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


object NavigationDestination {
    const val CUSTOMER = "ग्राहकहरू"
    const val ITEMS = "सामानहरू"
    const val SETTING = "सेटिङ"
    const val SPLASH_SCREEN = "Splash Screen"
}