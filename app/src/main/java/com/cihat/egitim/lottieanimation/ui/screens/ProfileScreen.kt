package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab

@Composable
fun ProfileScreen(
    onPro: () -> Unit,
    onAuth: () -> Unit,
    onSettings: () -> Unit,
    onFolders: () -> Unit,
    onSupport: () -> Unit,
    onRate: () -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfileItem(Icons.Default.Star, "Pro Ol", onPro) }
            item { ProfileItem(Icons.Default.AccountCircle, "Giriş/Kayıt", onAuth) }
            item { ProfileItem(Icons.Default.Settings, "Ayarlar", onSettings) }
            item { ProfileItem(Icons.Default.Folder, "Klasörlerim", onFolders) }
            item { ProfileItem(Icons.Default.Chat, "Canlı Destek", onSupport) }
            item { ProfileItem(Icons.Default.ThumbUp, "Google Play'de Oy Ver", onRate) }
        }
    }
}

@Composable
private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text)
    }
}
