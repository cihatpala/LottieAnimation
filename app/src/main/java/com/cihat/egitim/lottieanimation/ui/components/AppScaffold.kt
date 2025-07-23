package com.cihat.egitim.lottieanimation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp

enum class BottomTab { HOME, EXPLORE, PROFILE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    bottomTab: BottomTab? = null,
    onTabSelected: (BottomTab) -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    drawerContent: @Composable (closeDrawer: () -> Unit) -> Unit = {},
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bottomPadding = if (bottomTab != null) 80.dp else 0.dp
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.padding(bottom = bottomPadding),
                shape = RectangleShape
            ) {
                drawerContent { scope.launch { drawerState.close() } }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = actions
                )
            },
            bottomBar = {
                bottomTab?.let { tab ->
                    NavigationBar {
                        NavigationBarItem(
                            selected = tab == BottomTab.HOME,
                            onClick = { onTabSelected(BottomTab.HOME) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = tab == BottomTab.EXPLORE,
                            onClick = { onTabSelected(BottomTab.EXPLORE) },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                            label = { Text("Explore") }
                        )
                        NavigationBarItem(
                            selected = tab == BottomTab.PROFILE,
                            onClick = { onTabSelected(BottomTab.PROFILE) },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") }
                        )
                    }
                }
            }
        ) { inner ->
            Box(modifier = Modifier.padding(inner)) { content() }
        }
    }
}
