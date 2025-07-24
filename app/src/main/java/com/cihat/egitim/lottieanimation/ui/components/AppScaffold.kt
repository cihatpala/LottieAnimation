package com.cihat.egitim.lottieanimation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BottomTab { HOME, EXPLORE, PROFILE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    drawerState: DrawerState,
    bottomTab: BottomTab? = null,
    onTabSelected: (BottomTab) -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    drawerContent: @Composable (closeDrawer: () -> Unit) -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomPadding = if (bottomTab != null) 80.dp else 0.dp
    val isDrawerVisible by remember { derivedStateOf { drawerState.targetValue == DrawerValue.Open } }
    var dragStart by remember { mutableFloatStateOf(-1f) }

    Box(
        modifier = Modifier.pointerInput(isDrawerVisible.value) {
            detectHorizontalDragGestures(
                onDragStart = { dragStart = it.x },
                onDragEnd = { dragStart = -1f },
                onHorizontalDrag = { _, dragAmount ->
                    if (dragStart in 0f..40f && dragAmount > 10 && drawerState.isClosed) {
                        scope.launch { drawerState.open() }
                        dragStart = -1f
                    }
                    if (dragAmount < -10 && drawerState.isOpen) {
                        scope.launch { drawerState.close() }
                        dragStart = -1f
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        Crossfade(targetState = drawerState.isClosed, label = "menuIcon") { closed ->
                            IconButton(onClick = { scope.launch { if (closed) drawerState.open() else drawerState.close() } }) {
                                Icon(if (closed) Icons.Default.Menu else Icons.Default.MenuOpen, contentDescription = "Menu")
                            }
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
                            onClick = {
                                scope.launch { drawerState.close() }
                                onTabSelected(BottomTab.HOME)
                            },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") }
                        )
                        NavigationBarItem(
                            selected = tab == BottomTab.EXPLORE,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onTabSelected(BottomTab.EXPLORE)
                            },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                            label = { Text("Explore") }
                        )
                        NavigationBarItem(
                            selected = tab == BottomTab.PROFILE,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onTabSelected(BottomTab.PROFILE)
                            },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") }
                        )
                    }
                }
            }
        ) { inner ->
            Box(modifier = Modifier.padding(inner)) { content() }
        }

        if (isDrawerVisible.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { scope.launch { drawerState.close() } }
            )
        }

        AnimatedVisibility(
            visible = isDrawerVisible.value,
            enter = slideInHorizontally { -it },
            exit = slideOutHorizontally { -it }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .padding(bottom = bottomPadding)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                drawerContent { scope.launch { drawerState.close() } }
            }
        }
    }
}
