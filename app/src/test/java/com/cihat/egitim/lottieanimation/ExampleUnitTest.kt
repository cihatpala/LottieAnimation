package com.cihat.egitim.lottieanimation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class ExampleUnitTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun animationClickTogglesIsPlaying() {
        val isPlaying: MutableState<Boolean> = mutableStateOf(false)
        composeRule.setContent {
            AnimationApp(modifier = Modifier, onIsPlayingChanged = { isPlaying.value = it })
        }

        composeRule.onNodeWithTag("animation").performClick()

        composeRule.runOnIdle {
            assertTrue(isPlaying.value)
        }
    }
}

