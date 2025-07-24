package com.cihat.egitim.lottieanimation.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cihat.egitim.lottieanimation.R
import com.cihat.egitim.lottieanimation.data.Question
import com.cihat.egitim.lottieanimation.ui.components.AppScaffold

@Composable
fun QuizScreen(
    question: Question?,
    isAnswerVisible: Boolean,
    onReveal: () -> Unit,
    onAnswer: (Boolean) -> Unit,
    onQuit: () -> Unit
) {
    if (question == null) return

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_box_no_circle))

    val infiniteTransition = rememberInfiniteTransition()
    val progressValue by infiniteTransition.animateFloat(
        initialValue = 0.23f,
        targetValue = if (!isAnswerVisible) 0.345f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse,
        )
    )

    val progressNormal by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isAnswerVisible
    )

    AppScaffold(
        title = "Quiz",
        showBack = true,
        onBack = onQuit,
        onMenu = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAnswerVisible) {
                Text(
                    text = question.text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(width = 500.dp, height = 400.dp)
                    .clickable { if (!isAnswerVisible) onReveal() },
                contentAlignment = Alignment.Center
            ) {
                if (!isAnswerVisible) {
                    Text(
                        text = "${question.text}\nClick for answer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                LottieAnimation(
                    composition = composition,
                    progress = { if (isAnswerVisible) 0.23f + (progressNormal * 0.77f) else progressValue },
                    modifier = Modifier.fillMaxSize()
                )

                if (isAnswerVisible) {
                    Text(
                        text = question.answer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (isAnswerVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onAnswer(true) }) { Text("Doğru") }
                    Button(onClick = { onAnswer(false) }) { Text("Yanlış") }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onQuit) { Text("Quit") }
        }
    }
}
