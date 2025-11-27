package com.example.munchkin_app.ui.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

enum class InputType {
    FREE_TEXT,       // Sin validación especial
    SSID_LIST,       // Limite por número de comas
    IP_ADDRESS,      // Formato de dirección IP
}

@Composable
fun TextFieldWithHint(
    modifier: Modifier = Modifier,
    name: String,
    hint: String,
    value: String,
    type: InputType = InputType.FREE_TEXT,
    onValueChange: (String) -> Unit
) {
    val fieldShape = RoundedCornerShape(8.dp)
    val maxCommas = 19
    val addition = if (value.isNotEmpty()) 1 else 0

    val keyboardType = remember(type) {
        if (type == InputType.IP_ADDRESS) KeyboardType.Number else KeyboardType.Text
    }

    TextField(
        modifier = Modifier
            .padding(horizontal = 60.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = fieldShape
            )
            .fillMaxWidth(),
        value = value,
        onValueChange = {newValue ->
            val isValid = when (type) {

                InputType.SSID_LIST -> {
                    val commaCount = newValue.count { it == ',' }
                    val isDeleting = newValue.length < value.length
                    commaCount <= maxCommas || isDeleting
                }

                InputType.IP_ADDRESS -> {
                    val isDeleting = newValue.length < value.length
                    newValue.all { it.isDigit() || it == '.' } || isDeleting
                }

                InputType.FREE_TEXT -> {
                    true
                }
            }

            if (isValid) {
                onValueChange(newValue)
            }
        },
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
        maxLines = 3,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )

    val fullHint = when (type) {
        InputType.SSID_LIST -> {
            val currentCommaCount = value.count { it == ',' }

            if (currentCommaCount >= maxCommas) "You reached the max amount of SSID's"
            else
                "SSID's: ${currentCommaCount + addition} / ${maxCommas + 1}.\n$hint"
        }
        InputType.IP_ADDRESS -> {
            hint
        }
        else -> {
            hint
        }
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        text = fullHint,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Black,
        textAlign = TextAlign.Center,
    )
}