package com.example.munchkin_app.ui.common.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.munchkin_app.navigation.Destination

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var selectedDestination by rememberSaveable { mutableIntStateOf(Destination.HOME.ordinal) }

    // Actualiza selectedDestination basado en la ruta actual
    Destination.entries.forEachIndexed { index, destination ->
        if (currentRoute == destination.route) {
            selectedDestination = index
        }
    }

    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0XFFFFD8E4),
                    selectedTextColor = Color.Black
                ),
                selected = selectedDestination == index,
                onClick = {
                    if (selectedDestination != index) {
                        navController.navigate(destination.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                        selectedDestination = index
                    }
                },
                icon = {
                    Icon(
                        tint = MaterialTheme.colorScheme.onBackground,
                        painter = painterResource(destination.icon),
                        contentDescription = stringResource(destination.contentDescription)
                    )
                },
                label = { Text(stringResource(destination.labelResId)) }
            )
        }
    }
}