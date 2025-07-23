package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@Composable
fun ProfileScreen(
    showBack: Boolean,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Profile",
        showBack = showBack,
        onBack = onBack,
        bottomTab = BottomTab.PROFILE,
        onTabSelected = onTab
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile Page")
        }
    }
}
