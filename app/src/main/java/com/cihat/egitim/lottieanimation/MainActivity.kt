package com.cihat.egitim.lottieanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LottieAnimationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimationApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AnimationApp(modifier: Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.animation_box_no_circle))

    // To switch between the start and destination frames of the animation
    val infiniteTransition = rememberInfiniteTransition()
    val progressValue by infiniteTransition.animateFloat(
        initialValue = 0.23f,
        targetValue = if (!isPlaying) 0.345f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse,
        )
    )

    val progressNormal by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Question Text Clicked
        if (isPlaying) {
            Text(
                text = "Question 1 xyz?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lottie animation
        Box(
            modifier = Modifier
                .size(width = 500.dp, height = 400.dp)
                .clickable { isPlaying = true },
            contentAlignment = Alignment.Center
        ) {
            //Question Text - No Click
            if (!isPlaying) {
                Text(
                    text = "Question 1 xyz?\nClick for answare",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            LottieAnimation(
                composition = composition,
                progress = { if (isPlaying) 0.23f + (progressNormal * 0.77f) else progressValue },
                modifier = Modifier.fillMaxSize()
            )

            if (isPlaying) {
                Text(
                    text = "Answare 1",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    AnimationApp(modifier = Modifier)
}
