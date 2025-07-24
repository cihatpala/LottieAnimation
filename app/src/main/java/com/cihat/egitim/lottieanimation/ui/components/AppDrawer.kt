package com.cihat.egitim.lottieanimation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    isLoggedIn: Boolean,
    onClose: () -> Unit,
    onProfileInfo: () -> Unit,
    onPro: () -> Unit,
    onAuth: () -> Unit,
    onSettings: () -> Unit,
    onFolders: () -> Unit,
    onSupport: () -> Unit,
    onRate: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Kapat",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        DrawerItem(Icons.Default.Person, "Profilim", onProfileInfo)
        DrawerItem(Icons.Default.Star, "Pro Ol", onPro)
        if (!isLoggedIn) {
            DrawerItem(Icons.Default.AccountCircle, "Giriş/Kayıt", onAuth)
        }
        DrawerItem(Icons.Default.Settings, "Ayarlar", onSettings)
        DrawerItem(Icons.Default.Folder, "Klasörlerim", onFolders)
        DrawerItem(Icons.Default.Chat, "Canlı Destek", onSupport)
        DrawerItem(Icons.Default.ThumbUp, "Google Play'de Oy Ver", onRate)
        if (isLoggedIn) {
            DrawerItem(Icons.Default.Logout, "Çıkış Yap", onLogout)
        }
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(16.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}

