package com.example.munchkin_app.ui.screens.wifi.analyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.common.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun Analyzer(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MunchkinScreens.ApplicationLayout(navRouteBack = "wifi", navController = navController) {innerPadding ->
            AnalyzerContent(innerPadding)
        }
    }
}

@Composable
fun AnalyzerContent(innerPaddingValues: PaddingValues){
    var selectedOption by remember { mutableStateOf(DestinationOptions.SD) }

    Column (modifier = Modifier.padding(innerPaddingValues)) {
        Text(
            text = "WiFi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Text(
            text = "Analyzer",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProportionalSpacer(0.03f)

        // Componente de botones segmentados
        AnalyzerDestination(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            selectedOption = selectedOption,
            onSelectionChanged = { newOption ->
                selectedOption = newOption
                // Ejecutar acción específica según la opción seleccionada
                when (newOption) {
                    DestinationOptions.SD -> {
                        handleSerialConnection()
                    }
                    DestinationOptions.INTERNAL -> {
                        handleBluetoothConnection()
                    }
                }
            },
        )
    }
}

// Funciones para manejar las acciones de cada opción
private fun handleSerialConnection() {
    println("Iniciando conexión Serial...")
}

private fun handleBluetoothConnection() {
    println("Iniciando conexión Bluetooth...")
}

enum class DestinationOptions(
    val label: String,
) {
    SD("SD"),
    INTERNAL("Internal")
}

@Composable
fun AnalyzerDestination (
    modifier: Modifier = Modifier,
    selectedOption: DestinationOptions, // Corregido el tipo
    onSelectionChanged: (DestinationOptions) -> Unit, // Corregido el tipo
) {
    Text(
        text = "Destination",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )

    SingleChoiceSegmentedButtonRow (
        space = -4.dp,
        modifier = modifier
    ){
        DestinationOptions.entries.forEachIndexed { index, option ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0XFF72BA63),
                    activeContentColor = Color.White,
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = DestinationOptions.entries.size // Corregido
                ),
                onClick = {
                    onSelectionChanged(option)
                },
                selected = option == selectedOption,
                label = { Text(option.label) },
            )
        }
    }
}

@Preview
@Composable
fun AnalyzerPreview(){
    MunchkinappTheme {
        Analyzer(rememberNavController())
    }
}