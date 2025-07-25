package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.google.firebase.auth.FirebaseUser
import com.cihat.egitim.lottieanimation.data.StoredUser

@Composable
fun UserProfileScreen(
    user: FirebaseUser?,
    storedUser: StoredUser?,
    onBack: () -> Unit,
    bottomTab: BottomTab,
    onMenu: () -> Unit,
    onTab: (BottomTab) -> Unit = {}
) {
    AppScaffold(
        title = "Profilim",
        showBack = true,
        onBack = onBack,
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab
    ) {
        val infoName = user?.displayName ?: storedUser?.name
        val infoEmail = user?.email ?: storedUser?.email
        val infoPhoto = user?.photoUrl?.toString() ?: storedUser?.photoUrl

        if (infoName == null && infoEmail == null && infoPhoto == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kullanıcı bulunamadı")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                infoPhoto?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.size(16.dp))
                }
                infoName?.let { Text(it) }
                infoEmail?.let { Text(it) }
            }
        }
    }
}
