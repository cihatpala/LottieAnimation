package com.cihat.egitim.lottieanimation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.ColorFilter
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun AuthScreen(
    onGoogle: (String) -> Unit,
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    val context = LocalContext.current
    val authorizationClient = remember { Identity.getAuthorizationClient(context) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        result.data?.let { intent ->
            try {
                val authResult = authorizationClient.getAuthorizationResultFromIntent(intent)
                authResult.toGoogleSignInAccount()?.idToken?.let(onGoogle)
            } catch (_: Exception) {
            }
        }
    }
    val authorizationRequest = remember {
        AuthorizationRequest.Builder()
            .setRequestedScopes(listOf(Scope(Scopes.EMAIL), Scope(Scopes.PROFILE)))
            .requestOfflineAccess(context.getString(R.string.default_web_client_id))
            .build()
    }
    AppScaffold(
        title = "Auth",
        showBack = true,
        onBack = onBack
    ) {
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
                modifier = Modifier.size(250.dp),
                colorFilter = ColorFilter.tint(
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
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
                        .aspectRatio(1f)
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
                        .aspectRatio(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(Modifier.height(4.dp))
                        Text("Kaydol")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                authorizationClient.authorize(authorizationRequest)
                    .addOnSuccessListener { result ->
                        if (result.hasResolution()) {
                            result.pendingIntent?.let { pendingIntent ->
                                launcher.launch(
                                    IntentSenderRequest.Builder(pendingIntent).build()
                                )
                            }
                        } else {
                            result.toGoogleSignInAccount()?.idToken?.let(onGoogle)
                        }
                    }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = "Google"
                )
                Spacer(Modifier.width(8.dp))
                Text("Google ile Giriş Yap")
            }
        }
    }
}
