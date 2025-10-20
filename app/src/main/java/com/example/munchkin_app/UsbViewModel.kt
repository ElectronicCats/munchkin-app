package com.example.munchkin_app

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsbViewModel @Inject constructor(
    private val usbHelper: UsbHelper
) : ViewModel() {

    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

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