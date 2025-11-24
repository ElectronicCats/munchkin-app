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
    // Mapa para callbacks por messageId
    private val responseCallbacks = mutableMapOf<Int, (Main.MainResponse) -> Unit>()
    private var parserInstalled = false  // Flag para instalar solo una vez

    init {
        // Instalar parser global una sola vez
        installGlobalProtobufParser()
    }

    // -------------------------------------------------------
    // 🔥 Parser robusto (compatible con nanopb)
    // -------------------------------------------------------

    private fun installGlobalProtobufParser() {
        usbHelper.setProtobufCallback { bytes ->
            if (bytes.size < 2) return@setProtobufCallback

            try {
                val response = Main.MainResponse.parseFrom(bytes)
                Log.d("ProtobufRepository", "📩 Respuesta global recibida: $response (messageId: ${response.messageId})")

                if (response.hasAnalyzer()) {
                    val analyzerData = response.analyzer
                    val networks = analyzerData.networksList
                    val totalPackets = analyzerData.totalPacketCount
                    deviceRepo.wifiNetworks.value = networks
                    deviceRepo.totalPackets.value = totalPackets
                    Log.d("ProtobufRepository", "📡 Analyzer actualizado: ${networks.size} redes, ${totalPackets} paquetes")
                }

                // Procesar campos globales
                deviceRepo.deviceStatus.value = response.status
                deviceRepo.deviceCounterId.value = response.messageId

                // Llamar al callback específico si existe
                responseCallbacks[response.messageId]?.invoke(response)
                // Remover el callback después de usarlo (opcional, para evitar leaks)
                responseCallbacks.remove(response.messageId)

            } catch (e: Exception) {
                Log.e("ProtobufRepository", "ERROR: $e")
                val ascii = bytes.map { it.toInt().toChar() }.joinToString("")
                Log.d("ProtobufRepository", "📄 Mensaje ASCII recibido: $ascii")
                usbManager.status.value = ascii
                // Workaround: Parsear datos de analyzer desde ASCII si es el caso
                if (ascii.contains("analyzer_rpc") && ascii.contains("Redes:") && ascii.contains("Paquetes Totales:")) {
                    val redesMatch = Regex("Redes: (\\d+)").find(ascii)
                    val paquetesMatch = Regex("Paquetes Totales: (\\d+)").find(ascii)
                    if (redesMatch != null && paquetesMatch != null) {
                        val redes = redesMatch.groupValues[1].toIntOrNull() ?: 0
                        val paquetes = paquetesMatch.groupValues[1].toIntOrNull() ?: 0
                        // Simular respuesta protobuf (actualizar repositorio)
                        deviceRepo.totalPackets.value = paquetes
                        // Para redes, necesitarías una lista simulada o esperar datos reales
                        Log.d("ProtobufRepository", "🔧 Workaround: Redes=$redes, Paquetes=$paquetes")
                    }
                }
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
        // Instalar parser global solo la primera vez
        if (!parserInstalled) {
            installGlobalProtobufParser()
            parserInstalled = true
        }
        // Registrar el callback para este messageId
        responseCallbacks[messageId] = onResponse
        // Construir y enviar el mensaje
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
            messageId = 3,
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
            messageId = 4,
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
            messageId = 5,
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

    fun setNetwork(bssid: String){
        sendRequest(
            messageId = 6,
            requestBuilder = {
                setDeauthSelectTarget(Deauth.DeauthSelectTargetRequest.newBuilder()
                    .setBssid(bssid)
                    .build()
                )
            },
            onResponse = {Log.d("Deauth", "Network selected $bssid")}
        )
    }

    fun setAttackType(index: Int) {
        Log.d("Repo", "Repo index = $index")
        val protoType = when (index) {
            0 -> Deauth.DeauthAttackType.DEAUTH_TYPE_BROADCAST
            1 -> Deauth.DeauthAttackType.DEAUTH_TYPE_ROGUE_AP
            2 -> Deauth.DeauthAttackType.DEAUTH_TYPE_COMBINED
            else -> Deauth.DeauthAttackType.DEAUTH_TYPE_BROADCAST
        }

        sendRequest(
            messageId = 7,
            requestBuilder = {
                setDeauthSetAttack(Deauth.DeauthSetAttackRequest.newBuilder()
                    .setType(protoType)
                    .build()
                )
            },
            onResponse = {Log.d("Deauth", "Attack type set to $protoType")}
        )
    }

    fun deauthStartAttackRequest() {
        sendRequest(
            messageId = 8,
            requestBuilder = {
                setDeauthStart(Deauth.DeauthStartAttackRequest.getDefaultInstance())
            },
            onResponse = {Log.d("Deauth", "Attack started")}
        )
    }

    fun deauthStopAttackRequest() {
        sendRequest(
            messageId = 8,
            requestBuilder = {
                setDeauthStop(Deauth.DeauthStopRequest.getDefaultInstance())
            },
            onResponse = {Log.d("Deauth", "Attack started")}
        )
    }

}
