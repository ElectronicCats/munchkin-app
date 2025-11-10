package com.example.munchkin_app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween

@OptIn(ExperimentalAnimationApi::class)
object NavigationTransitions {

    private const val DURATION = 300

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
}
