package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun PublicUserProfileScreen(
    username: String,
    fullName: String,
    photoUrl: String?,
    onBack: () -> Unit,
    bottomTab: BottomTab,
    onMenu: () -> Unit,
    onTab: (BottomTab) -> Unit = {},
) {
    AppScaffold(
        title = username,
        showBack = true,
        onBack = onBack,
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            photoUrl?.takeIf { it.isNotBlank() }?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.size(16.dp))
            }
            Text(fullName)
        }
    }
}
