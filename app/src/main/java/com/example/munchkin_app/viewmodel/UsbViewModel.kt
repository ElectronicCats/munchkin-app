package com.example.munchkin_app.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchkin_app.data.usb.UsbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsbViewModel @Inject constructor(
    private val usbHelper: UsbHelper
) : ViewModel() {

    private val _usbDevices = MutableStateFlow<List<String>>(emptyList())
    val usbDevices = _usbDevices.asStateFlow()

    private val _status = MutableStateFlow("Idle")
    val status = _status.asStateFlow()

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

    init {
        usbHelper.onDevicesChanged = {
            viewModelScope.launch {  // Asegura que la actualización ocurra en una corutina
                detectDevices()
            }
        }
        usbHelper.registerReceiver()
    }

    override fun onCleared() {
        super.onCleared()
        usbHelper.unregisterUsbReceiver()  // Desregistra solo al destruir el ViewModel
    }

    fun detectDevices() {
        val entries = usbHelper.detectDevices()
        _usbDevices.value = entries.map { it.name }
        _status.value = if (entries.isEmpty()) "No USB devices detected" else "Devices detected"
    }

    fun connectDevice(device: String): String {
        usbHelper.detectAndGetPermission { msg ->
            _status.value = msg
        }
        _selectedDevice.value = device
        return "Connected to $device"
    }

    fun disconnectDevice() {
        usbHelper.disconnect { msg -> _status.value = msg }
        _selectedDevice.value = null
        restartReceiver()  // Reinicia el receiver aquí para recuperar detección
    }
    fun restartReceiver() {
        usbHelper.unregisterUsbReceiver()
        usbHelper.registerReceiver()
        Log.d("UsbViewModel", "Receiver reiniciado después de disconnect")
    }

    fun appendLog(text: String) {
        _logs.add(text)
    }

    fun registerReceiver() = usbHelper.registerReceiver()
    fun unregisterReceiver() = usbHelper.unregisterUsbReceiver()

    fun detectAndGetPermission() =
        usbHelper.detectAndGetPermission(::appendLog)

    fun readSerial() =
        usbHelper.readSerial(::appendLog)

    fun startCounter() =
        usbHelper.startCounter(::appendLog)

    fun stopCounter() =
        usbHelper.stopCounter(::appendLog)
}