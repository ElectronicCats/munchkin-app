package com.example.munchkin_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchkin_app.data.usb.UsbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import minino.rpc.Main
import javax.inject.Inject

@HiltViewModel
class UsbViewModel @Inject constructor(
    private val usbHelper: UsbHelper
) : ViewModel() {

    //Imports
    //DeviceRepository
    private val deviceRepository = DeviceRepository()
    val deviceName: StateFlow<String?> = deviceRepository.deviceName
    val deviceVersion: StateFlow<String?> = deviceRepository.deviceVersion
    val deviceStatus: StateFlow<Main.Status> = deviceRepository.deviceStatus
    val deviceCounterId: StateFlow<Int?> = deviceRepository.deviceCounterId
    //UsbManager
    private val usbManager = UsbManager(usbHelper, viewModelScope, deviceRepository)
    val usbDevices = usbManager.usbDevices.asStateFlow()
    val status = usbManager.status.asStateFlow()
    val selectedDevice = usbManager.selectedDevice.asStateFlow()
    val connectionStatus: StateFlow<String> = usbManager.connectionStatus.asStateFlow()
    //ProtobufRepository
    private val protobufRepository = ProtobufRepository(
        usbHelper = usbHelper,
        deviceRepo = deviceRepository,
        usbManager = usbManager,
        scope = viewModelScope,

    )

    //Logs
    private val _logs = mutableStateListOf<String>()

    init {
        usbHelper.onDevicesChanged = {
            viewModelScope.launch {
                detectDevices()
                if (usbHelper.detectDevices().isEmpty()) {
                    usbManager.disconnect()
                    deviceRepository.clear()
                }
            }
        }
        usbHelper.registerReceiver()
    }
    override fun onCleared() = super.onCleared().also { usbHelper.unregisterUsbReceiver() }
    fun connectDevice(device: String) = usbManager.connect(device) {protobufRepository.requestAboutInfoRepeatedly()}
    fun disconnectDevice() = usbManager.disconnect()
    fun detectDevices() = usbManager.detect()
    private fun appendLog(text: String) = _logs.add(text)
    fun registerReceiver() = usbHelper.registerReceiver()
    fun unregisterReceiver() = usbHelper.unregisterUsbReceiver()
    fun detectAndGetPermission() = usbHelper.detectAndGetPermission(::appendLog)
}
