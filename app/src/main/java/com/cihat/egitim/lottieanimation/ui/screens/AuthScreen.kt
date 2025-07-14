package com.cihat.egitim.lottieanimation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun AuthScreen(
    onGoogle: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            account.idToken?.let { onGoogle(it) }
        } catch (_: Exception) {
        }
    }
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val signInClient = GoogleSignIn.getClient(context, gso)
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
                modifier = Modifier.size(250.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Uygulamaya giriş yaparak farklı cihazlardan kaldığınız yerden devam edebilirsiniz.",
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Login, contentDescription = null)
                        Spacer(Modifier.height(4.dp))
                        Text("Giriş Yap")
                    }
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(Modifier.height(4.dp))
                        Text("Kaydol")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = { launcher.launch(signInClient.signInIntent) }) {
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
