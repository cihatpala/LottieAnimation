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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
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
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onProfileInfo: () -> Unit,
    showBack: Boolean,
    onBack: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    AppScaffold(
        title = "Profile",
        showBack = showBack,
        onBack = onBack,
        onMenu = { onTab(BottomTab.MENU) },
        bottomTab = BottomTab.PROFILE,
        onTabSelected = onTab,
        actions = {
            if (isLoggedIn) {
                IconButton(onClick = onProfileInfo) {
                    Icon(Icons.Default.Person, contentDescription = "Profilim")
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProfileItem(Icons.Default.Star, "Pro Ol", onPro) }
            if (!isLoggedIn) {
                item { ProfileItem(Icons.Default.AccountCircle, "Giriş/Kayıt", onAuth) }
            }
            item { ProfileItem(Icons.Default.Settings, "Ayarlar", onSettings) }
            item { ProfileItem(Icons.Default.Folder, "Klasörlerim", onFolders) }
            item { ProfileItem(Icons.Default.Chat, "Canlı Destek", onSupport) }
            item { ProfileItem(Icons.Default.ThumbUp, "Google Play'de Oy Ver", onRate) }
            if (isLoggedIn) {
                item { ProfileItem(Icons.Default.Logout, "Çıkış Yap", onLogout) }
            }
        }
    }
}

@Composable
private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text)
        }
    }
}
