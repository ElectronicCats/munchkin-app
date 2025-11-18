package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DeauthViewModel @Inject constructor(): ViewModel() {
    private val _attackIndex = MutableStateFlow(0)
    val attackIndex: StateFlow<Int> = _attackIndex

    fun updateAttackIndex(index: Int) {
        _attackIndex.value = index
    }
}