package com.example.munchkin_app.viewmodel.usb

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
    //Analyzer
    val wifiNetworks = deviceRepository.wifiNetworks.asStateFlow()
    val totalPackets = deviceRepository.totalPackets.asStateFlow()
    //Deauth
    val deauthNetworks = deviceRepository.deauthNetworks.asStateFlow()

    //ProtobufRepository
    private val protobufRepository = ProtobufRepository(
        usbHelper = usbHelper,
        deviceRepo = deviceRepository,
        usbManager = usbManager,
        scope = viewModelScope,
    )
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
    fun registerReceiver() = usbHelper.registerReceiver()
    fun unregisterReceiver() = usbHelper.unregisterUsbReceiver()

    //Analyzer
    fun startAnalyzer() = protobufRepository.startAnalyzer()
    fun setChannel(channel: Int) = protobufRepository.setAnalyzerChannel(channel)
    fun stopAnalyzer() = protobufRepository.stopAnalyzer()

    //Deauth
    fun startDeauthScan() = protobufRepository.startDeauthScan()

    fun clearDeauthNetworks() {deviceRepository.deauthNetworks.value = emptyList()}

    fun setDeauthTarget(bssid: String) = protobufRepository.setNetwork(bssid)

    fun setAttackType(index: Int) = protobufRepository.setAttackType(index)

    fun startDeauthAttack() = protobufRepository.deauthStartAttackRequest()

    fun deauthStopAttackRequest() = protobufRepository.deauthStopAttackRequest()

    //SSID SPAMMER
    fun ssidSpammerSetSsids(index: Int, ssids: List<String>) = protobufRepository.ssidSpammerSetSsids(index, ssids)

    fun ssidSpammerStartRequest() = protobufRepository.ssidSpammerStartRequest()

    fun ssidSpammerStopRequest() = protobufRepository.ssidSpammerStopRequest()
}
