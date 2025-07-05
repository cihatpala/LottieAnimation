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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.cihat.egitim.lottieanimation.viewmodel.QuizViewModel
import com.cihat.egitim.lottieanimation.viewmodel.UiState
import com.cihat.egitim.lottieanimation.ui.theme.LottieAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LottieAnimationTheme {
                QuizApp()
            }
        }
    }
}

@Composable
fun QuizApp(viewModel: QuizViewModel = viewModel()) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (val state = viewModel.uiState) {
            is UiState.SetupBoxes -> SetupScreen(Modifier.padding(innerPadding), viewModel)
            is UiState.AddQuestion -> AddQuestionScreen(Modifier.padding(innerPadding), viewModel)
            is UiState.BoxList -> BoxListScreen(Modifier.padding(innerPadding), viewModel)
            is UiState.Quiz -> QuizScreen(Modifier.padding(innerPadding), viewModel)
        }
    }
}

@Composable
fun SetupScreen(modifier: Modifier = Modifier, viewModel: QuizViewModel) {
    var text by remember { mutableStateOf("4") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Box count") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { text.toIntOrNull()?.let { viewModel.setBoxCount(it) } }) {
            Text("Start")
        }
    }
}

@Composable
fun AddQuestionScreen(modifier: Modifier = Modifier, viewModel: QuizViewModel) {
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var boxText by remember { mutableStateOf("1") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = boxText,
            onValueChange = { boxText = it },
            label = { Text("Box number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val index = boxText.toIntOrNull()?.minus(1) ?: 0
                viewModel.addQuestion(questionText, answerText, index)
                questionText = ""
                answerText = ""
                boxText = "1"
            }) {
                Text("Add")
            }
            Button(onClick = { viewModel.backToBoxes() }) { Text("Done") }
        }
    }
}

@Composable
fun BoxListScreen(modifier: Modifier = Modifier, viewModel: QuizViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.boxes.forEachIndexed { index, box ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Box ${index + 1}: ${box.size} questions")
                Button(onClick = { viewModel.startQuiz(index) }) { Text("Quiz") }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.toAddQuestion() }) { Text("Add Question") }
    }
}

@Composable
fun QuizScreen(modifier: Modifier = Modifier, viewModel: QuizViewModel) {
    val question = viewModel.currentQuestion ?: return
    val isPlaying = viewModel.isAnswerVisible

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_box_no_circle))

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
        if (isPlaying) {
            Text(
                text = question.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(width = 500.dp, height = 400.dp)
                .clickable { if (!isPlaying) viewModel.revealAnswer() },
            contentAlignment = Alignment.Center
        ) {
            if (!isPlaying) {
                Text(
                    text = "${question.text}\nClick for answer",
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
                    text = question.answer,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        if (isPlaying) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.onAnswerSelected(true) }) { Text("Doğru") }
                Button(onClick = { viewModel.onAnswerSelected(false) }) { Text("Yanlış") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.backToBoxes() }) { Text("Quit") }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    QuizApp()
}
