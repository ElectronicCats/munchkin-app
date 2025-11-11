package com.example.munchkin_app.ui.screens.welcome

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingPagerWithButton(navController: NavController) {
    //Required variables for correct functionality
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )
    val scope = rememberCoroutineScope()
    var isChecked by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        //Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> WelcomeTextOne()
                1 -> ScreenTwo()
                2 -> WelcomeTextThree(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }
                )
            }
        }

        //Button to skip to last screen
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(4.dp)
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
                elevation = null,
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.Welcome_skip),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
            //Dots that show the current screen
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.secondary
                else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(20.dp)
                )
            }
            //Button to advance to next screen
            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage == 2) {
                            if (isChecked) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context,
                                    context.getString(R.string.Welcome_toast), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val nextPage = (pagerState.currentPage + 1).coerceAtMost(2)
                            pagerState.animateScrollToPage(nextPage)
                        }
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
                    text = stringResource(R.string.Welcome_next),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
//Preview to see the screen in Android Studio
@Preview
@Composable
fun WelcomeViewPager(){
    MunchkinappTheme {
        OnboardingPagerWithButton(rememberNavController())
    }
}