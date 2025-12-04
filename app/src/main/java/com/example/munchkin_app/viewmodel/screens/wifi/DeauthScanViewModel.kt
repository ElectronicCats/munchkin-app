package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DeauthScanViewModel @Inject constructor(): ViewModel() {
    private val _selectedChannel = MutableStateFlow("Channel 1")
    val selectedChannel: StateFlow<String> = _selectedChannel

    private val _scanChannel = MutableStateFlow<String?>(null)
    val scanChannel: StateFlow<String?> = _scanChannel

    fun setScanChannel(channel: String) {
        _scanChannel.value = channel
    }

    fun clearScanChannel() {
        _scanChannel.value = null
    }

    fun updateChannel(channel: String) {
        _selectedChannel.value = channel
    }
}