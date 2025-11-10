package com.example.munchkin_app.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

object Cards {
    @Composable
    fun CustomCard(
        modifier: Modifier = Modifier.Companion,
        title: String,
        text: String,
        iconDescription: String,
        navController: NavHostController,
        navRoute: String,
        icon: Int
    ){
        Card(
            shape = CardDefaults.shape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = modifier
                .padding(start = 8.dp)
                .clickable(onClick = { navController.navigate(navRoute) })
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = iconDescription,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp),

                )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Companion.Normal,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Companion.Normal,
                textAlign = TextAlign.Companion.Center,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}