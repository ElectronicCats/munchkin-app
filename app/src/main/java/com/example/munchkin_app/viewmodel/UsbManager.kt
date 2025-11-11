package com.example.munchkin_app.viewmodel

import android.util.Log
import com.example.munchkin_app.data.usb.UsbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import minino.rpc.Main

class UsbManager (
    private val usbHelper: UsbHelper,
    private val scope: CoroutineScope,
    private val deviceRepository: DeviceRepository,
) {
    val usbDevices = MutableStateFlow<List<String>>(emptyList())
    val status = MutableStateFlow("Idle")
    val connectionStatus = MutableStateFlow("Not Connected")

    val selectedDevice = MutableStateFlow<String?>(null)

    fun connect(device: String, requestAboutInfoRepeatedly: () -> Unit) {
        selectedDevice.value = device
        usbHelper.onUsbPermissionGranted = {
            scope.launch {
                val connected = usbHelper.ensurePortOpen { status.value = it }
                if (connected) {
                    connectionStatus.value = "Connected to $device"
                    Log.d("UsbViewModel", "Aqui deberia de hacer la solicitud")
                    requestAboutInfoRepeatedly()
                } else {
                    connectionStatus.value = "Connection failed"
                }
            }
        }
        usbHelper.detectAndGetPermission { status.value = it }
    }

    fun disconnect() {
        usbHelper.disconnect { msg -> status.value = msg }
        selectedDevice.value = null
        connectionStatus.value = "Not Connected"
        restartReceiver()
        deviceRepository.deviceStatus.value = Main.Status.STATUS_UNKNOWN
    }

    fun detect() {
        val entries = usbHelper.detectDevices()
        usbDevices.value = entries.map { it.name }
        status.value =
            if (entries.isEmpty()) "No USB devices detected" else "Devices detected"
    }

    private fun restartReceiver() {
        usbHelper.unregisterUsbReceiver()
        usbHelper.registerReceiver()
    }


}