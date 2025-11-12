package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CaptiveViewModel @Inject constructor(): ViewModel(){
    private val _selectedModeIndex = MutableStateFlow(0)
    val selectedModeIndex: StateFlow<Int> = _selectedModeIndex
    private val _selectedSdDestinationIndex = MutableStateFlow(0)
    val selectedSdDestinationIndex: StateFlow<Int> = _selectedSdDestinationIndex
    private val _selectedChannel = MutableStateFlow("Channel 1")
    val selectedChannel: StateFlow<String> = _selectedChannel

    fun updateModeIndex(index: Int) {
        _selectedModeIndex.value = index
    }
    fun updateSdDestinationIndex(index: Int) {
        _selectedSdDestinationIndex.value = index
    }

    fun updateChannel(channel: String) {
        _selectedChannel.value = channel
    }
}