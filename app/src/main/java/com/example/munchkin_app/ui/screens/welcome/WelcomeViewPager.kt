package com.example.munchkin_app.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingPagerWithButton() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 } // ✅ OK
    )
    val scope = rememberCoroutineScope() // para animaciones

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
        ) { page ->
            when (page) {
                0 -> ScreenOne()
                1 -> ScreenTwo()
                2 -> ScreenThree()
            }
        }

        // Botón para avanzar a la siguiente pantalla
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()



        ) {
            Button(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(2) }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = null, // 🔹 sin sombra
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Skip",
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
            }


            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.secondary
                else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(24.dp)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        val nextPage = (pagerState.currentPage + 1).coerceAtMost(2)
                        pagerState.animateScrollToPage(nextPage)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = null, // 🔹 sin sombra
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Next",
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun WelcomeViewPager(){
    MunchkinappTheme {
        OnboardingPagerWithButton()
    }
}