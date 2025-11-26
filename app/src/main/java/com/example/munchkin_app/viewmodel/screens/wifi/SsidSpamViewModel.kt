package com.example.munchkin_app.viewmodel.screens.wifi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.munchkin_app.data.datastore.SsidConfigMap
import com.example.munchkin_app.data.datastore.SsidSpamConfigDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SsidSpamViewModel @Inject constructor(
    private val dataStore: SsidSpamConfigDataStore
): ViewModel() {

    val allConfigs: StateFlow<SsidConfigMap> = dataStore.configsFlow
        .stateIn(
            // 🔑 ARGUMENTO 1: El ámbito donde se ejecuta (ViewModel)
            scope = viewModelScope,
            // 🔑 ARGUMENTO 2: Regla de compartición (se ejecuta solo cuando hay UI observando)
            started = SharingStarted.WhileSubscribed(5000),
            // ARGUMENTO 3: El valor inicial
            initialValue = SsidConfigMap()
        )

    fun saveNewConfig(name: String, rawText: String) {
        val ssids = rawText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        viewModelScope.launch {
            dataStore.saveSsids(name, ssids) // Pasa el nombre y la lista
        }
    }

    fun deleteConfig(listName: String) {
        viewModelScope.launch {
            dataStore.deleteSsidList(listName)
        }
    }


}