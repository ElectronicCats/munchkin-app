package com.example.munchkin_app.ui.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun TextFieldWithHint(name: String, hint: String) {
    val fieldShape = RoundedCornerShape(8.dp)

    TextField(
        modifier = Modifier
            .padding(horizontal = 60.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = fieldShape
            )
            .fillMaxWidth(),
        state = rememberTextFieldState(),
        shape = fieldShape,
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3)
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        text = hint,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Black,
        textAlign = TextAlign.Center,
    )
}