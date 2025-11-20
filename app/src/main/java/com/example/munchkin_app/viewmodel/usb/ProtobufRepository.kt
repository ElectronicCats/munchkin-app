package com.example.munchkin_app.viewmodel.usb

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import minino.rpc.Main
import minino.about.About
import minino.analyzer.Analyzer
import minino.deauth.Deauth
import com.example.munchkin_app.data.usb.UsbHelper

class ProtobufRepository(
    private val usbHelper: UsbHelper,
    private val deviceRepo: DeviceRepository,
    private val usbManager: UsbManager,
    private val scope: CoroutineScope
) {

    // -------------------------------------------------------
    // 🔥 Parser robusto (compatible con nanopb)
    // -------------------------------------------------------

    private fun installProtobufParser(onResponse: (Main.MainResponse) -> Unit) {

        usbHelper.setProtobufCallback { bytes ->

            if (bytes.size < 2) return@setProtobufCallback

            try {
                // Nanopb usa length-prefix → parseFrom funciona directo
                val response = Main.MainResponse.parseFrom(bytes)

                Log.d("ProtobufRepository", "📩 Respuesta recibida: $response")

                // Estado global
                deviceRepo.deviceStatus.value = response.status
                deviceRepo.deviceCounterId.value = response.messageId

                // Entregar respuesta arriba
                onResponse(response)

                // 🔥 Seguir escuchando después de cada respuesta
                installProtobufParser(onResponse)

            } catch (e: Exception) {

                // Si el frame NO era protobuf → intentar ASCII limpio
                val ascii = bytes.map { it.toInt().toChar() }
                    .filter { it.isLetterOrDigit() || it in ". -" }
                    .joinToString("")

                if (ascii.isNotBlank()) {
                    usbManager.status.value = ascii
                }

                // 🔥 Mantener vivo el parser aunque haya error
                installProtobufParser(onResponse)
            }
        }
    }

    // -------------------------------------------------------
    // 🔥 Request general reutilizable
    // -------------------------------------------------------

    fun sendRequest(
        messageId: Int = 1,
        requestBuilder: Main.MainRequest.Builder.() -> Unit,
        onResponse: (Main.MainResponse) -> Unit = {}
    ) {
        if (!usbHelper.ensurePortOpen { usbManager.status.value = it }) return

        // Siempre activar parser antes del request
        installProtobufParser(onResponse)

        // Construir el mensaje
        val request = Main.MainRequest.newBuilder()
            .setMessageId(messageId)
            .apply(requestBuilder)
            .build()

        usbHelper.writeToSerial(request) { usbManager.status.value = it }
    }

    // -------------------------------------------------------
    // 🔥 Request: About Info
    // -------------------------------------------------------

    fun requestAboutInfo() {
        sendRequest(
            messageId = 1,
            requestBuilder = {
                setAbout(About.AboutRequest.getDefaultInstance())
            },
            onResponse = { response ->
                if (response.hasAbout()) {
                    val about = response.about
                    deviceRepo.deviceName.value = about.productName
                    deviceRepo.deviceVersion.value = about.version
                } else {
                    Log.w("ProtobufRepository", "⚠️ Respuesta sin campo 'about'")
                }
            }
        )
    }

    // Igual que la tuya, no se cambia nada
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
        }
    }

    // -------------------------------------------------------
    // 🔥 Analyzer
    // -------------------------------------------------------

    fun startAnalyzer() {
        sendRequest(
            messageId = 2,
            requestBuilder = {
                setAnalyzerStart(Analyzer.AnalyzerStartRequest.getDefaultInstance())
            },
            onResponse = { response ->
                Log.d("ProtobufRepository", "📡 startAnalyzer -> Recibido: ${response.payloadCase}")
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
            },
            onResponse = { response ->
                if (response.hasAnalyzer()) {
                    val analyzerData = response.analyzer
                    val networks = analyzerData.networksList
                    val totalPackets = analyzerData.totalPacketCount

                    deviceRepo.wifiNetworks.value = networks
                    deviceRepo.totalPackets.value = totalPackets

                    Log.d("ProtobufRepository", "📡 Redes: ${networks.size}")
                } else {
                    Log.w("ProtobufRepository", "⚠️ Respuesta sin 'analyzer'")
                }
            }
        )
    }

    // -------------------------------------------------------
    // 🔥 Deauth Scan
    // -------------------------------------------------------

    fun startDeauthScan() {
        sendRequest(
            messageId = 4,
            requestBuilder = {
                setDeauthScan(Deauth.DeauthScanRequest.getDefaultInstance())
            },
            onResponse = { response ->
                if (response.hasDeauthScanResults()) {
                    val results = response.deauthScanResults
                    val networks = results.apsList

                    deviceRepo.deauthNetworks.value = networks

                    Log.d("ProtobufRepository", "📡 Deauth -> ${networks.size} redes")
                } else {
                    Log.w("ProtobufRepository", "⚠️ Respuesta sin campo 'deauth'")
                }
            }
        )
    }
}
