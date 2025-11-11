package com.example.munchkin_app.ui.common.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.munchkin_app.R

object MunchkinScreens {
    @SuppressLint("ConfigurationScreenWidthHeight")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MunchkinLayout(
        navController: NavHostController,
        content: @Composable (PaddingValues) -> Unit
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Text(
                            "Munchkin",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier
                                .size(screenWidth * 0.05f),
                            onClick = { /* do something */ }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(screenWidth * 0.05f),
                                painter = painterResource(R.drawable.icon_menu),
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier
                                .size(screenWidth * 0.1f),
                            onClick = { /* do something */ }) {
                            Icon(
                                modifier = Modifier
                                    .size(screenWidth * 0.1f),
                                painter = painterResource(R.drawable._2_isotipo_ec),
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            content(innerPadding)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ApplicationLayout(
        navController: NavHostController,
        navRouteBack: String,
        content: @Composable (PaddingValues) -> Unit
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                    title = {
                        Text(
                            "Munchkin",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(navRouteBack) }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_arrowback),
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_info),
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}