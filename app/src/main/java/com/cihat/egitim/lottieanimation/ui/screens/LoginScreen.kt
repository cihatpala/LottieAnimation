package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalContext
import com.cihat.egitim.lottieanimation.utils.NetworkUtils
import com.cihat.egitim.lottieanimation.ui.components.PrimaryAlert
import android.widget.Toast
import androidx.compose.foundation.layout.Box

@Composable
fun LoginScreen(
    onLogin: (String, String, (Boolean) -> Unit) -> Unit,
    onBack: () -> Unit,
    onForgot: () -> Unit,
    onSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AppScaffold(
        title = "Giriş Yap",
        showBack = true,
        onBack = onBack,
        onMenu = { }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Kullanıcı adı veya e-mail") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        android.widget.Toast.makeText(
                            context,
                            "Lütfen e-posta ve şifre giriniz",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else if (!NetworkUtils.isConnected(context)) {
                        showError = true
                    } else {
                        isLoading = true
                        onLogin(email, password) { success ->
                            isLoading = false
                            // Only show the connection error when login failed
                            // due to missing network rather than auth issues
                            showError = !success && !NetworkUtils.isConnected(context)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Giriş Yap")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Şifremi unuttum.",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onForgot)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Henüz hesabınız yok mu?")
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Kaydol",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(onClick = onSignup)
                        .padding(4.dp)
                )
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
