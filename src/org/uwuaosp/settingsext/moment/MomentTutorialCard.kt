/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.uwuaosp.settingsext.moment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieOnCompositionLoadedListener
import org.uwuaosp.settingsext.R
import kotlin.math.abs

private data class TutorialStep(val animation: Int, val description: Int)

private val tutorialSteps = listOf(
    TutorialStep(R.raw.moment_tutorial_move,
        R.string.moment_tutorial_move_description),
    TutorialStep(R.raw.moment_tutorial_resize,
        R.string.moment_tutorial_resize_description),
    TutorialStep(R.raw.moment_tutorial_compact,
        R.string.moment_tutorial_compact_description),
    TutorialStep(R.raw.moment_tutorial_back,
        R.string.moment_tutorial_back_description),
    TutorialStep(R.raw.moment_tutorial_fullscreen,
        R.string.moment_tutorial_fullscreen_description),
    TutorialStep(R.raw.moment_tutorial_close,
        R.string.moment_tutorial_close_description),
)

@Composable
internal fun MomentTutorialCard(lifecycle: Lifecycle) {
    val pager = rememberPagerState { tutorialSteps.size }
    val completed = remember { mutableStateMapOf<Int, Boolean>() }
    var resumed by remember { mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            resumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxWidth(),
            key = { tutorialSteps[it].animation },
            verticalAlignment = Alignment.Top,
        ) { index ->
            TutorialPage(
                step = tutorialSteps[index],
                active = resumed && pager.settledPage == index && !pager.isScrollInProgress,
                finished = completed[index] == true,
                onFinished = { completed[index] = true },
                onReplay = { completed[index] = false },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).clearAndSetSemantics { },
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(tutorialSteps.size) { index ->
                val selection = (1f - abs(pager.currentPage + pager.currentPageOffsetFraction - index))
                    .coerceIn(0f, 1f)
                Box(Modifier.size(width = (6f + 10f * selection).dp, height = 6.dp)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = .25f + .6f * selection,
                        ),
                        CircleShape,
                    ))
            }
        }
    }
}

@Composable
internal fun NavHandleDoubleTapTutorialCard(lifecycle: Lifecycle) {
    var finished by remember { mutableStateOf(false) }
    var resumed by remember { mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            resumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        TutorialPage(
            step = TutorialStep(R.raw.moment_nav_handle_double_tap,
                R.string.moment_nav_handle_double_tap_animation_description),
            active = resumed,
            finished = finished,
            onFinished = { finished = true },
            onReplay = { finished = false },
            showDescription = false,
        )
    }
}

@Composable
private fun TutorialPage(
    step: TutorialStep,
    active: Boolean,
    finished: Boolean,
    onFinished: () -> Unit,
    onReplay: () -> Unit,
    showDescription: Boolean = true,
) {
    val context = LocalContext.current
    var ready by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    val player = remember(context, step.animation) {
        LottieAnimationView(context).apply {
            repeatCount = 0
            scaleType = ImageView.ScaleType.FIT_CENTER
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }
    DisposableEffect(player) {
        val loaded = LottieOnCompositionLoadedListener { ready = true }
        val listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) = onFinished()
        }
        player.addLottieOnCompositionLoadedListener(loaded)
        player.addAnimatorListener(listener)
        player.setFailureListener { failed = true }
        player.setAnimation(step.animation)
        onDispose {
            player.removeLottieOnCompositionLoadedListener(loaded)
            player.removeAnimatorListener(listener)
            player.setFailureListener { }
            player.cancelAnimation()
        }
    }
    LaunchedEffect(player, ready, active, finished, failed) {
        if (!ready || failed) return@LaunchedEffect
        if (finished) {
            player.pauseAnimation()
            player.progress = 1f
        } else if (active) {
            if (ValueAnimator.areAnimatorsEnabled()) player.resumeAnimation()
            else {
                player.progress = 1f
                onFinished()
            }
        } else player.pauseAnimation()
    }

    Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp)) {
        val previewHeight = (LocalConfiguration.current.screenHeightDp.dp * .38f).coerceIn(220.dp, 320.dp)
        Box(Modifier.fillMaxWidth().height(previewHeight).padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center) {
            AndroidView(factory = { player }, modifier = Modifier.fillMaxSize())
            if (!ready && !failed) CircularProgressIndicator()
            if (failed) {
                Text(stringResource(R.string.moment_tutorial_unavailable), textAlign = TextAlign.Center)
            }
            if (finished && !failed) {
                IconButton(
                    onClick = { player.progress = 0f; onReplay() },
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Icon(painterResource(R.drawable.ic_replay_animation),
                        contentDescription = stringResource(R.string.replay_animation),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
        if (showDescription) {
            Text(stringResource(step.description), style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .8f))
        }
    }
}
