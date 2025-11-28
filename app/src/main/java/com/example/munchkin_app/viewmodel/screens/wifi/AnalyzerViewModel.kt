package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AnalyzerViewModel @Inject constructor(): ViewModel(){
    private val _selectedDestinationIndex = MutableStateFlow(0)
    val selectedDestinationIndex: StateFlow<Int> = _selectedDestinationIndex
    private val _selectedChannel = MutableStateFlow("Channel 1")
    val selectedChannel: StateFlow<String> = _selectedChannel

    private val _scanChannel = MutableStateFlow<String?>(null)
    val scanChannel: StateFlow<String?> = _scanChannel

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running

    private val _showStoppedMessage = MutableStateFlow(false)
    val showStoppedMessage: StateFlow<Boolean> = _showStoppedMessage
    fun setScanChannel(channel: String) {
        _scanChannel.value = channel
    }

    fun clearScanChannel() {
        _scanChannel.value = null
    }

    fun updateDestinationIndex(index: Int) {
        _selectedDestinationIndex.value = index
    }
    fun updateChannel(channel: String) {
        _selectedChannel.value = channel
    }

    fun updateRunning(running: Boolean) {
        _running.value = running
    }

    fun updateShowStoppedMessage(show: Boolean) {
        _showStoppedMessage.value = show
    }
}