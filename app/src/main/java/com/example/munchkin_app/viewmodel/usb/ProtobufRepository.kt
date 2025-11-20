package com.example.munchkin_app.viewmodel.usb

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import minino.rpc.Main
import com.example.munchkin_app.data.usb.UsbHelper
import minino.about.About
import minino.analyzer.Analyzer
import minino.deauth.Deauth

class ProtobufRepository(
    private val usbHelper: UsbHelper,
    private val deviceRepo: DeviceRepository,
    private val usbManager: UsbManager,
    private val scope: CoroutineScope
) {
    // Variable para almacenar el Job del listener, para poder cancelarlo si es necesario
    private var listenerJob: Job? = null

    // --- Lógica de Lectura Constante y Deserialización ---

    init {
        // 💡 Iniciar el listener de puerto serial inmediatamente al crear el repositorio
        startListeningForSerialData()
    }

    /**
     * Inicia un Coroutine que escucha constantemente la llegada de datos
     * del puerto serial y los procesa como mensajes Protobuf.
     */
    private fun startListeningForSerialData() {
        // Si ya hay un Job corriendo, lo cancelamos antes de empezar uno nuevo (seguridad)
        listenerJob?.cancel()

        listenerJob = scope.launch {
            Log.d("ProtobufRepository", "🎧 Iniciando listener permanente de datos seriales.")

            // 💡 CONFIGURAMOS EL CALLBACK PERMANENTE AQUÍ
            usbHelper.setProtobufCallback { bytes ->
                // Este código se ejecuta cada vez que UsbHelper detecta un mensaje completo
                processProtobufResponse(bytes)
            }

            // Simplemente mantenemos el Coroutine vivo para que el callback de UsbHelper
            // siga activo mientras la aplicación está abierta.
            try {
                // Delay infinito para mantener la Coroutine activa
                while (true) { delay(1000L) }
            } catch (e: Exception) {
                Log.d("ProtobufRepository", "Listener de puerto serial terminado: ${e.message}")
            }
        }
    }

    /**
     * Procesa los bytes recibidos para deserializar el mensaje Protobuf.
     */
    private fun processProtobufResponse(bytes: ByteArray) {
        // 💡 DEBUG: FORZANDO LOG DE ALTO NIVEL PARA CONFIRMAR LLAMADA
        Log.e("ProtobufRepository", "--- PROTOBUF REPOSITORY CALLED --- Iniciando procesamiento de ${bytes.size} bytes.")

        if (bytes.size < 5) {
            Log.d("ProtobufRepository", "Bytes descartados por tamaño insuficiente: ${bytes.size}")
            return
        }

        try {
            val response = Main.MainResponse.parseFrom(bytes)
            // Si el parseo es exitoso, verás este log
            Log.d("ProtobufRepository", "✅ Respuesta recibida (Payload Case: ${response.payloadCase.name}):\n${response.toString()}")

            // Actualizar estado general del dispositivo
            deviceRepo.deviceStatus.value = response.status
            deviceRepo.deviceCounterId.value = response.messageId

            // 💡 DISPATCH DE RESPUESTA: Aquí tienes que manejar todas las respuestas posibles

            // 1. Respuesta de About (ID 1)
            if (response.hasAbout()) {
                handleAboutResponse(response.about)
            }
            // 2. Respuesta de Analyzer (ID 2 o 3)
            else if (response.hasAnalyzer()) {
                handleAnalyzerResponse(response.analyzer)
            }
            // 3. Respuesta de Deauth Scan (ID 4)
            // 🎯 CORRECCIÓN: Revisar explícitamente si la respuesta es DeauthScanResults
            else if (response.hasDeauthScanResults()) {
                handleDeauthScanResponse(response.deauthScanResults)
            }
            // 4. Respuesta Simple de Estado (para comandos como START/STOP o SET CHANNEL)
            else if (response.payloadCase == Main.MainResponse.PayloadCase.PAYLOAD_NOT_SET) {
                Log.d("ProtobufRepository", "Respuesta simple de estado OK/ERROR.")
            }

        } catch (e: Exception) {
            val hexBytes = bytes.joinToString(" ") { String.format("%02X", it) }
            Log.e("ProtobufRepository", "❌ Bytes recibidos (Hex): $hexBytes")
            Log.e("ProtobufRepository", "❌ Error al parsear Protobuf. Tamaño: ${bytes.size}. Error: ${e.message}", e)

            val ascii = bytes.map { it.toInt().toChar() }
                .filter { it.isLetterOrDigit() || it in ". -" }
                .joinToString("")
            if (ascii.isNotBlank()) {
                // Si el error de parseo ocurre, y el contenido parece ASCII, lo mostramos
                usbManager.status.value = "Datos no Protobuf (Error de Parseo o ASCII): $ascii"
            }
        }
    }

    // --- Métodos de Manejo de Respuestas Específicas ---

    private fun handleAboutResponse(about: About.AboutResponse) {
        Log.d("ProtobufRepository", "Handling About Response...")
        deviceRepo.deviceName.value = about.productName
        deviceRepo.deviceVersion.value = about.version
        // 💡 DEBUG EXTRA: Confirmar que los valores fueron asignados
        Log.d("ProtobufRepository", "About Info SET: ${about.productName} v${about.version}")
    }

    private fun handleAnalyzerResponse(analyzerData: Analyzer.AnalyzerData) {
        // Log para confirmar que esta función se ejecuta
        Log.d("ProtobufRepository", "Handling Analyzer Response, received ${analyzerData.networksList.size} networks.")
        deviceRepo.wifiNetworks.value = analyzerData.networksList
        deviceRepo.totalPackets.value = analyzerData.totalPacketCount
        Log.d("ProtobufRepository", "📡 Recibidas ${analyzerData.networksList.size} redes Wi-Fi por Analyzer.")
    }

    private fun handleDeauthScanResponse(deauthData: Deauth.DeauthScanResults) {
        Log.d("ProtobufRepository", "Handling Deauth Scan Results...")
        val aps = deauthData.apsList
        deviceRepo.apList.value = aps
        Log.d("ProtobufRepository", "📡 Recibidas ${aps.size} redes Wi-Fi por Deauth.")

        // El log detallado que querías (reconfirmando los datos)
        for (n in aps) {
            // NOTA: Si ves el error de UTF-8, la representación del BSSID en el ESP32 debe ser Hex/ASCII
            Log.d("ProtobufRepository", "-> SSID: ${n.ssid} | BSSID: ${n.bssid}")
        }
    }

    // --- Lógica de Envío (Simplificada) ---

    /**
     * Envia un request Protobuf configurable.
     */
    fun sendRequest(
        messageId: Int = 1,
        requestBuilder: Main.MainRequest.Builder.() -> Unit
    ) {
        // Asegura que el puerto esté abierto (usbHelper.ensurePortOpen)
        if (!usbHelper.ensurePortOpen { usbManager.status.value = it }) return

        // Construir el request
        val request = Main.MainRequest.newBuilder()
            .setMessageId(messageId)
            .apply(requestBuilder)
            .build()

        // 💡 DEBUG: Ver el request antes de enviarlo
        Log.d("ProtobufRepository", "⬆️ Enviando Request ID $messageId (Payload: ${request.payloadCase.name})")

        // usbHelper.writeToSerial se encarga de enviar los bytes.
        usbHelper.writeToSerial(request) { usbManager.status.value = it }
    }

    // --- Métodos de Comando Específicos ---

    /**
     * Comando: Solicitar información de la placa ("About")
     */
    fun requestAboutInfo() {
        sendRequest(
            messageId = 1,
            requestBuilder = {
                setAbout(About.AboutRequest.getDefaultInstance())
            }
        )
    }

    /**
     * Ejemplo de reintento automático (idéntico al tuyo actual)
     */
    fun requestAboutInfoRepeatedly() {
        scope.launch {
            if (deviceRepo.deviceName.value != null &&
                deviceRepo.deviceVersion.value != null) return@launch

            var attempts = 0
            while ((deviceRepo.deviceName.value == null ||
                        deviceRepo.deviceVersion.value == null) && attempts < 5) {
                Log.d("UsbViewModel", "Intento #$attempts de obtener AboutInfo")
                requestAboutInfo()
                attempts++
                delay(1000L)
            }
            // 💡 DEBUG: Log para saber si el bucle terminó sin éxito
            if (attempts >= 5 && (deviceRepo.deviceName.value == null || deviceRepo.deviceVersion.value == null)) {
                Log.e("UsbViewModel", "🚨 Fallo al obtener AboutInfo después de $attempts intentos.")
            }
        }
    }

    /**
     * Comando: Iniciar el escaneo de redes Deauth.
     * 🎯 REEMPLAZA a requestNetwork para mayor claridad.
     */
    fun requestDeauthScan(){
        sendRequest(
            messageId = 4,
            requestBuilder = {
                setDeauthScan(Deauth.DeauthScanRequest.getDefaultInstance())
            }
        )
    }

    fun startAnalyzer() {
        sendRequest(
            messageId = 2,
            requestBuilder = {
                setAnalyzerStart(Analyzer.AnalyzerStartRequest.getDefaultInstance())
            }
        )
    }

    fun setAnalyzerChannel(channel: Int) {
        sendRequest(
            messageId = 11,
            requestBuilder = {
                setAnalyzerSetChannel(
                    Analyzer.AnalyzerSetChannelRequest.newBuilder()
                        .setChannel(channel)
                        .build()
                )
            }
        )
    }

    fun stopAnalyzer() {
        sendRequest(
            messageId = 3,
            requestBuilder = {
                setAnalyzerStop(Analyzer.AnalyzerStopRequest.getDefaultInstance())
            }
        )
    }
}