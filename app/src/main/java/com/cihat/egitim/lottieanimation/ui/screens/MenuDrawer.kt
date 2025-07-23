package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cihat.egitim.lottieanimation.ui.navigation.Screen
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel

@Composable
fun MenuDrawer(
    onPro: () -> Unit,
    onAuth: () -> Unit,
    onSettings: () -> Unit,
    onFolders: () -> Unit,
    onSupport: () -> Unit,
    onRate: () -> Unit,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onProfileInfo: () -> Unit,
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(Color.LightGray, CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Close")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 56.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MenuItem(Icons.Default.Person, "Profilim", onProfileInfo) }
            item { MenuItem(Icons.Default.Star, "Pro Ol", onPro) }
            if (!isLoggedIn) {
                item { MenuItem(Icons.Default.AccountCircle, "Giriş/Kayıt", onAuth) }
            }
            item { MenuItem(Icons.Default.Settings, "Ayarlar", onSettings) }
            item { MenuItem(Icons.Default.Folder, "Klasörlerim", onFolders) }
            item { MenuItem(Icons.Default.Chat, "Canlı Destek", onSupport) }
            item { MenuItem(Icons.Default.ThumbUp, "Google Play'de Oy Ver", onRate) }
            if (isLoggedIn) {
                item { MenuItem(Icons.Default.Logout, "Çıkış Yap", onLogout) }
            }
        }
    }
}

@Composable
fun AppDrawer(navController: NavHostController, authViewModel: AuthViewModel, onClose: () -> Unit) {
    MenuDrawer(
        onPro = {},
        onAuth = { navController.navigate(Screen.Auth.route) },
        onSettings = { navController.navigate(Screen.Settings.route) },
        onFolders = { navController.navigate(Screen.FolderList.route) },
        onSupport = {},
        onRate = {},
        isLoggedIn = authViewModel.currentUser != null,
        onLogout = {
            authViewModel.logout(navController.context)
            navController.navigate(Screen.QuizList.route) {
                popUpTo(Screen.QuizList.route) { inclusive = true }
            }
        },
        onProfileInfo = { navController.navigate(Screen.MyProfile.route) },
        onClose = onClose
    )
}

@Composable
private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
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
