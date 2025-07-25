package com.cihat.egitim.lottieanimation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.ColorFilter
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.ui.components.BottomTab
import com.cihat.egitim.lottieanimation.ui.components.PrimaryAlert
import com.cihat.egitim.lottieanimation.utils.NetworkUtils

@Composable
fun AuthScreen(
    onGoogle: () -> Unit,
    onBack: () -> Unit,
    onLogin: () -> Unit,
    bottomTab: BottomTab,
    onMenu: () -> Unit,
    onTab: (BottomTab) -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        isLoading = false
        if (res.resultCode == Activity.RESULT_OK) {
            onGoogle()
        }
    }
    val providers = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
    AppScaffold(
        title = "Auth",
        showBack = true,
        onBack = onBack,
        onMenu = onMenu,
        bottomTab = bottomTab,
        onTabSelected = onTab
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Image(
                painter = painterResource(id = R.drawable.knowledge_logo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .sizeIn(maxWidth = 250.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Uygulamaya giriş yaparak farklı cihazlardan kaldığınız yerden devam edebilirsiniz.",
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onLogin,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(Modifier.height(4.dp))
                        Text("Giriş Yap")
                    }
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(Modifier.height(4.dp))
                        Text("Kaydol")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                if (!NetworkUtils.isConnected(context)) {
                    showError = true
                } else {
                    isLoading = true
                    val intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setCredentialManagerEnabled(false)
                        .build()
                    launcher.launch(intent)
                }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google"
                )
                Spacer(Modifier.width(8.dp))
                Text("Google ile Giriş Yap")
            }

        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (showError) {
            PrimaryAlert(
                title = "Uyarı",
                message = "İnternet bağlantınızı kontrol ediniz",
                onDismiss = { showError = false }
            )
        }
        }
    }
}
