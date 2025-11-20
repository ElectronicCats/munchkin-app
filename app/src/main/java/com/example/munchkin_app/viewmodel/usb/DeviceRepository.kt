package com.example.munchkin_app.viewmodel.usb

import kotlinx.coroutines.flow.MutableStateFlow
import minino.analyzer.Analyzer
import minino.deauth.Deauth
import minino.rpc.Main

class DeviceRepository {
    val deviceName = MutableStateFlow<String?>(null)
    val deviceVersion = MutableStateFlow<String?>(null)
    val deviceStatus = MutableStateFlow(Main.Status.STATUS_UNKNOWN)
    val deviceCounterId = MutableStateFlow<Int?>(null)
    val wifiNetworks = MutableStateFlow<List<Analyzer.WifiNetwork>>(emptyList())
    val totalPackets = MutableStateFlow(0)

    val apList = MutableStateFlow<List<Deauth.DeauthAP>>(emptyList())


    fun clear() {
        deviceName.value = null
        deviceVersion.value = null
        deviceStatus.value = Main.Status.STATUS_UNKNOWN
        deviceCounterId.value = null
        wifiNetworks.value = emptyList()
    }
}