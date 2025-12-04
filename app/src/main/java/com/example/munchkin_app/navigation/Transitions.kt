package com.example.munchkin_app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

@OptIn(ExperimentalAnimationApi::class)
object NavigationTransitions {

    private const val DURATION = 300
    private const val SHORT_DURATION = 250

    val fadeSlideIn: (AnimatedContentTransitionScope<*>.() -> EnterTransition) = {
        fadeIn(animationSpec = tween(DURATION, easing = LinearEasing)) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(DURATION, easing = EaseIn)
                )
    }
    val fadeSlideOut: (AnimatedContentTransitionScope<*>.() -> ExitTransition) = {
        fadeOut(animationSpec = tween(DURATION, easing = LinearEasing)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(DURATION, easing = EaseOut)
                )
    }

    val fadeIn: (AnimatedContentTransitionScope<*>.() -> EnterTransition) = {
        fadeIn(animationSpec = tween(SHORT_DURATION, easing = LinearEasing))
    }
    val fadeOut: (AnimatedContentTransitionScope<*>.() -> ExitTransition) = {
        fadeOut(animationSpec = tween(SHORT_DURATION, easing = LinearEasing))
    }

    val scaleIn: (AnimatedContentTransitionScope<*>.() -> EnterTransition) = {
        scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearOutSlowInEasing
            )
        )
    }

    val scaleOut: (AnimatedContentTransitionScope<*>.() -> ExitTransition) = {
        scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutLinearInEasing
            )
        )
    }

}
