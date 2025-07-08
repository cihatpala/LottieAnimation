package com.cihat.egitim.lottieanimation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold
import com.cihat.egitim.lottieanimation.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LottieAnimationTheme {
                    AuthScreen(
                        onLogin = { e, p ->
                            authViewModel.login(e, p) { success ->
                                if (success) goMain()
                            }
                        },
                        onRegister = { e, p ->
                            authViewModel.register(e, p) { success ->
                                if (success) goMain()
                            }
                        },
                        onBack = { findNavController().navigateUp() }
                    )
                }
            }
        }
    }

    private fun goMain() {
        findNavController().navigate(com.cihat.egitim.lottieanimation.R.id.setupFragment)
    }
}

@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    AppScaffold(
        title = "Auth",
        showBack = true,
        onBack = onBack
    ) {
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
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            if (email.isBlank() || password.isBlank()) {
                android.widget.Toast.makeText(context, "Email and password required", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                onLogin(email, password)
            }
        }) { Text("Login") }
        Button(onClick = {
            if (email.isBlank() || password.isBlank()) {
                android.widget.Toast.makeText(context, "Email and password required", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                onRegister(email, password)
            }
        }) { Text("Register") }
    }
    }
}
