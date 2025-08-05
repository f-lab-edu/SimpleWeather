package com.ben.simpleweather.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val name: String,
    @SerialName("name_ko")
    val nameKo: String,
    val state: String?,
    val country: String,
    val coord: Coord,
)