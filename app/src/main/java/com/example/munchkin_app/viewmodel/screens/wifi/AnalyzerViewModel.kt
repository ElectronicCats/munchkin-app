package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    fun updateDestinationIndex(index: Int) {
        _selectedDestinationIndex.value = index
    }
    fun updateChannel(channel: String) {
        _selectedChannel.value = channel
    }
}