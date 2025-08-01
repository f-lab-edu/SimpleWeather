package com.ben.simpleweather.data

data class City(
    val id: Int,
    val name: String,
    val nameKo: String,
    val state: String?,
    val country: String,
    val coord: Coord,
)