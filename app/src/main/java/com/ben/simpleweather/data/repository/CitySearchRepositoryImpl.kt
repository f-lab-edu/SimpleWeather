package com.ben.simpleweather.data.repository

import android.content.Context
import com.ben.simpleweather.data.City
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CitySearchRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CitySearchRepository {

    private val cities: List<City> by lazy {
        loadCitiesFromAssets()
    }

    override suspend fun searchCities(query: String): List<City> =
        withContext(Dispatchers.Default) {
            if (query.isBlank()) return@withContext emptyList()

            val jamoQuery = query.toJamoList().joinToString("")

            cities.filter { city ->
                val name = city.name
                val nameKo = city.nameKo
                val jamoKo = nameKo.toJamoList().joinToString("")

                name.contains(query, ignoreCase = true) ||
                        nameKo.contains(query) ||
                        jamoKo.startsWith(jamoQuery)
            }
        }

    // Load and parse JSON once
    private fun loadCitiesFromAssets(): List<City> {
        return context.assets.open("cities.json").bufferedReader().use {
            val jsonString = it.readText()
            Json.decodeFromString(jsonString)
        }
    }
}

fun String.toJamoList(): List<Char> {
    val result = mutableListOf<Char>()

    for (char in this) {
        if (char in '가'..'힣') {
            val syllableIndex = char.code - 0xAC00
            val cho = syllableIndex / (21 * 28)
            val jung = (syllableIndex % (21 * 28)) / 28
            val jong = syllableIndex % 28

            val choseong = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"[cho]
            val jungseong = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"[jung]

            result += choseong
            result += jungseong
            if (jong != 0) {
                val jongseong = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ"[jong - 1]
                result += jongseong
            }
        } else {
            result += char
        }
    }
    return result
}
