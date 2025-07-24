package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import androidx.compose.material3.DrawerState

@Composable
fun ProfileScreen(
    drawerState: DrawerState,
    onTab: (BottomTab) -> Unit,
    drawerContent: @Composable (closeDrawer: () -> Unit) -> Unit = {}
) {
    AppScaffold(
        title = "Profile",
        drawerState = drawerState,
        bottomTab = BottomTab.PROFILE,
        onTabSelected = onTab,
        drawerContent = drawerContent
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile Page")
        }
    }
}
