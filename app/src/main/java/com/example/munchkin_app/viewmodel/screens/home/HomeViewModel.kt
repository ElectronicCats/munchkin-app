package com.example.munchkin_app.viewmodel.screens.home

import androidx.lifecycle.ViewModel
import com.example.munchkin_app.ui.screens.home.components.ConnectionOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    private val _selectedOption = MutableStateFlow(ConnectionOption.SERIAL)
    val selectedOption: StateFlow<ConnectionOption> = _selectedOption

    private val _previousOption = MutableStateFlow<ConnectionOption?>(null)
    val previousOption: StateFlow<ConnectionOption?> = _previousOption

    fun updateSelectedOption(option: ConnectionOption) {
        _previousOption.value = _selectedOption.value
        _selectedOption.value = option
    }

//    fun resetOptions() {
//        _selectedOption.value = ConnectionOption.SERIAL
//        _previousOption.value = null
//    }
}