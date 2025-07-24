package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@Composable
fun ProfileScreen(
    bottomTab: BottomTab,
    onMenu: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Profile",
        showBack = false,
        onBack = {},
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile Page")
        }
    }
}
