package com.ben.simpleweather.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ben.simpleweather.data.City
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "city_storage")

class CityStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CityStorageRepository {

    private val key = stringPreferencesKey("saved_cities")

    override suspend fun getSavedCities(): List<City> {
        val prefs = context.dataStore.data.first()
        val json = prefs[key] ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun addCity(city: City) {
        val current = getSavedCities().toMutableList()
        current.add(city)
        saveCities(current)
    }

    override suspend fun removeCities(cities: List<City>) {
        val current = getSavedCities()
        val updated = current.filterNot { city -> cities.any { it.id == city.id } }
        saveCities(updated)
    }

    private suspend fun saveCities(cities: List<City>) {
        val updatedJson = Json.encodeToString(cities)
        context.dataStore.edit { prefs ->
            prefs[key] = updatedJson
        }
    }

    override suspend fun getCityById(id: Int): City? {
        return getSavedCities().find { it.id == id }
    }
}