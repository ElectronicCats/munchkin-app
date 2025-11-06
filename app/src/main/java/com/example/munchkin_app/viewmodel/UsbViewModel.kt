package com.example.munchkin_app.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchkin_app.data.usb.UsbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import minino.about.About
import minino.rpc.Main
import javax.inject.Inject

@HiltViewModel
class UsbViewModel @Inject constructor(
    private val usbHelper: UsbHelper
) : ViewModel() {

    private val _usbDevices = MutableStateFlow<List<String>>(emptyList())
    val usbDevices = _usbDevices.asStateFlow()

    private val _status = MutableStateFlow("Idle")
    val status = _status.asStateFlow()

    private val _aboutInfo = MutableStateFlow<Main.MainResponse?>(null)
    val aboutInfo: StateFlow<Main.MainResponse?> = _aboutInfo

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

    private val _deviceVersion = MutableStateFlow<String?>(null)
    val deviceVersion: StateFlow<String?> = _deviceVersion

    private val _deviceName = MutableStateFlow<String?>(null)
    val deviceName: StateFlow<String?> = _deviceName

    private val _deviceStatus = MutableStateFlow<Main.Status>(Main.Status.STATUS_UNKNOWN)
    val deviceStatus: StateFlow<Main.Status> = _deviceStatus

    private val _deviceCounterID = MutableStateFlow<Int?>(null)
    val deviceCounterID: StateFlow<Int?> = _deviceCounterID

    private val _connectionStatus = MutableStateFlow("Not Connected")
    val connectionStatus: StateFlow<String> = _connectionStatus

    init {
        usbHelper.onDevicesChanged = {
            viewModelScope.launch {
                detectDevices()
                if (usbHelper.detectDevices().isEmpty()) {
                    _connectionStatus.value = "Not Connected"
                    _deviceName.value = null
                    _deviceVersion.value = null
                    _selectedDevice.value = null
                }
            }
        }
        usbHelper.registerReceiver()
    }

    override fun onCleared() {
        super.onCleared()
        usbHelper.unregisterUsbReceiver()
    }

    fun detectDevices() {
        val entries = usbHelper.detectDevices()
        _usbDevices.value = entries.map { it.name }
        _status.value =
            if (entries.isEmpty()) "No USB devices detected" else "Devices detected"
    }

    fun connectDevice(device: String): String {
        _selectedDevice.value = device

        usbHelper.detectAndGetPermission { msg ->
            _status.value = msg
        }
        _connectionStatus.value = "Connected to $device"
        return _connectionStatus.value
    }

    fun disconnectDevice() {
        usbHelper.disconnect { msg -> _status.value = msg }
        _selectedDevice.value = null
        _connectionStatus.value = "Not Connected"
        restartReceiver()
    }

    private fun restartReceiver() {
        usbHelper.unregisterUsbReceiver()
        usbHelper.registerReceiver()
        Log.d("UsbViewModel", "Receiver reiniciado después de disconnect")
    }

    private fun appendLog(text: String) {
        _logs.add(text)
    }

    fun registerReceiver() = usbHelper.registerReceiver()
    fun unregisterReceiver() = usbHelper.unregisterUsbReceiver()
    fun detectAndGetPermission() = usbHelper.detectAndGetPermission(::appendLog)

    fun requestAboutInfoRepeatedly() {
        viewModelScope.launch {
            // Evita múltiples lanzamientos simultáneos
            if (_deviceName.value != null && _deviceVersion.value != null) return@launch

            var attempts = 0
            while ((_deviceName.value == null || _deviceVersion.value == null) && attempts < 5) {
                Log.d("UsbViewModel", "Intento #$attempts de obtener AboutInfo")
                requestAboutInfo()
                attempts++
                kotlinx.coroutines.delay(1000L) // espera 1s entre intentos
            }
            Log.d("UsbViewModel", "Finalizado intento de obtener AboutInfo (nombre=${_deviceName.value}, versión=${_deviceVersion.value})")
        }
    }


    /**
     * Envía una solicitud 'AboutRequest' al dispositivo y procesa la respuesta del ESP32.
     */
    fun requestAboutInfo() {
        Log.d("UsbViewModel", "requestAboutInfo llamado")
        if (!usbHelper.ensurePortOpen { _status.value = it }) return

        usbHelper.setProtobufCallback { bytes ->
            if (bytes.size < 5) return@setProtobufCallback  // ignorar ACKs u otros ruidos

            Log.d("UsbViewModel", "Bytes recibidos: ${bytes.joinToString(",")}")
            try {
                val response = Main.MainResponse.parseFrom(bytes)
                _deviceStatus.value = response.status
                _deviceCounterID.value = response.messageId
                if (response.hasAbout()) {
                    val about = response.about
                    _deviceName.value = about.productName
                    _deviceVersion.value = about.version
                } else {
                    Log.w("UsbViewModel", "⚠️ Respuesta sin campo 'about'")
                }
            } catch (e: Exception) {
                val ascii = bytes.map { it.toInt().toChar() }
                    .filter { it.isLetterOrDigit() || it in ". -" }
                    .joinToString("")
                if (ascii.isNotBlank()) {
                    _status.value = ascii
                    Log.d("UsbViewModel", "📜 Fallback ASCII: $ascii")
                }
            }
        }


        // Enviar request — el listener interno ya escuchará la respuesta
        val request = Main.MainRequest.newBuilder()
            .setMessageId(1)
            .setAbout(About.AboutRequest.newBuilder().build())
            .build()

        usbHelper.writeToSerial(request) { _status.value = it }
    }

}
