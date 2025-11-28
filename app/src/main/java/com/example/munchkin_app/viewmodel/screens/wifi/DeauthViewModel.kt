package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DeauthViewModel @Inject constructor(): ViewModel() {

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running

    private val _attackIndex = MutableStateFlow(0)
    val attackIndex: StateFlow<Int> = _attackIndex

    private val _selectedNetwork = MutableStateFlow("")
    val selectedNetwork: StateFlow<String> = _selectedNetwork

    private val _isNetworkSelected = MutableStateFlow(false)
    val isNetworkSelected: StateFlow<Boolean> = _isNetworkSelected

    private val _scanAttempted = MutableStateFlow(false)
    val scanAttempted: StateFlow<Boolean> = _scanAttempted


    fun updateRunning(isSelected: Boolean) {
        _running.value = isSelected
    }

    fun updateAttackIndex(index: Int) {
        _attackIndex.value = index
    }

    fun updateSelectedNetwork(network: String) {
        _selectedNetwork.value = network
    }

    fun updateIsNetworkSelected(isSelected: Boolean) {
        _isNetworkSelected.value = isSelected
    }

    fun updateScanAttempted(attempted: Boolean) {
        _scanAttempted.value = attempted
    }
}