package com.example.munchkin_app.viewmodel

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import minino.rpc.Main
import com.example.munchkin_app.data.usb.UsbHelper

class ProtobufRepository(
    private val usbHelper: UsbHelper,
    private val deviceRepo: DeviceRepository,
    private val usbManager: UsbManager,
    private val scope: CoroutineScope
) {

    /**
     * Envia un request Protobuf configurable y maneja su respuesta.
     */
    fun sendRequest(
        messageId: Int = 1,
        requestBuilder: Main.MainRequest.Builder.() -> Unit,
        onResponse: (Main.MainResponse) -> Unit = {}
    ) {
        if (!usbHelper.ensurePortOpen { usbManager.status.value = it }) return

        // Configurar callback de lectura
        usbHelper.setProtobufCallback { bytes ->
            if (bytes.size < 5) return@setProtobufCallback

            try {
                val response = Main.MainResponse.parseFrom(bytes)
                Log.d("ProtobufRepository", "✅ Respuesta recibida: ${response}")

                // Actualizar estado general
                deviceRepo.deviceStatus.value = response.status
                deviceRepo.deviceCounterId.value = response.messageId

                // Callback para el usuario (UI o lógica externa)
                onResponse(response)

            } catch (e: Exception) {
                val ascii = bytes.map { it.toInt().toChar() }
                    .filter { it.isLetterOrDigit() || it in ". -" }
                    .joinToString("")
                if (ascii.isNotBlank()) {
                    usbManager.status.value = ascii
                }
            }
        }

        // Construir el request
        val request = Main.MainRequest.newBuilder()
            .setMessageId(messageId)
            .apply(requestBuilder)
            .build()

        usbHelper.writeToSerial(request) { usbManager.status.value = it }
    }

    /**
     * Ejemplo de uso: request específico para "About"
     */
    fun requestAboutInfo() {
        sendRequest(
            messageId = 1,
            requestBuilder = {
                setAbout(minino.about.About.AboutRequest.getDefaultInstance())
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
                kotlinx.coroutines.delay(1000L)
            }
        }
    }
}
