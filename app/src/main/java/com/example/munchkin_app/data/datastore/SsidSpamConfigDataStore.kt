package com.example.munchkin_app.data.datastore

// WifiConfigDataStore.kt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wifi_config")

class SsidSpamConfigDataStore(context: Context) {

    private val dataStore = context.dataStore

    private val SSID_CONFIG_KEY = stringPreferencesKey("ssid_configs_json")


    // Función para obtener TODAS las configuraciones guardadas
    private suspend fun getAllConfigs(): SsidConfigMap {
        val jsonString = dataStore.data.map { preferences ->
            preferences[SSID_CONFIG_KEY] ?: "{}" // Devuelve un JSON vacío si no hay datos
        }.first() // Obtiene el valor una sola vez

        return try {
            Json.decodeFromString<SsidConfigMap>(jsonString)
        } catch (e: Exception) {
            SsidConfigMap() // En caso de error, devuelve un mapa vacío
        }
    }

    suspend fun saveSsids(listName: String, ssids: List<String>) {
        dataStore.edit { preferences ->
            val currentMap = getAllConfigs().configs.toMutableMap()

            currentMap[listName] = ssids

            val newJsonString = Json.encodeToString(SsidConfigMap(currentMap))
            preferences[SSID_CONFIG_KEY] = newJsonString
        }
    }

    suspend fun deleteSsidList(listName: String) {
        dataStore.edit { preferences ->
            val currentMap = getAllConfigs().configs.toMutableMap()

            currentMap.remove(listName)

            val newJsonString = Json.encodeToString(SsidConfigMap(currentMap))
            preferences[SSID_CONFIG_KEY] = newJsonString
        }
    }

    val configsFlow: Flow<SsidConfigMap> = dataStore.data
        .map { preferences ->
            val jsonString = preferences[SSID_CONFIG_KEY] ?: "{}"
            try {
                Json.decodeFromString<SsidConfigMap>(jsonString)
            } catch (e: Exception) {
                SsidConfigMap()
            }
        }
}

@Serializable
data class SsidConfigMap(
    // La llave es el "Name identificador" y el valor es la lista de SSIDs
    val configs: Map<String, List<String>> = emptyMap()
)