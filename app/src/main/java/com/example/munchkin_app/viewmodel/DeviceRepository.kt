package com.example.munchkin_app.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import minino.analyzer.Analyzer
import minino.rpc.Main

class DeviceRepository {
    val deviceName = MutableStateFlow<String?>(null)
    val deviceVersion = MutableStateFlow<String?>(null)
    val deviceStatus = MutableStateFlow(Main.Status.STATUS_UNKNOWN)
    val deviceCounterId = MutableStateFlow<Int?>(null)
    val wifiNetworks = MutableStateFlow<List<Analyzer.WifiNetwork>>(emptyList())

    fun updateWifiNetworks(newNetworks: List<Analyzer.WifiNetwork>) {
        wifiNetworks.value = newNetworks.toList()
    }

    fun clear() {
        deviceName.value = null
        deviceVersion.value = null
        deviceStatus.value = Main.Status.STATUS_UNKNOWN
        deviceCounterId.value = null
        wifiNetworks.value = emptyList()
    }
}