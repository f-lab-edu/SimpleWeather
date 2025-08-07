package com.ben.simpleweather

import com.ben.simpleweather.data.City
import com.ben.simpleweather.data.Coord
import com.ben.simpleweather.data.remote.dto.MainDto
import com.ben.simpleweather.data.remote.dto.WeatherDto
import com.ben.simpleweather.data.remote.dto.WeatherResponse
import com.ben.simpleweather.data.repository.CityStorageRepository
import com.ben.simpleweather.data.repository.WeatherRepository
import com.ben.simpleweather.features.main.WeatherListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherListViewModelTest {

    private lateinit var viewModel: WeatherListViewModel

    private val cityStorageRepo = mockk<CityStorageRepository>()
    private val weatherRepo = mockk<WeatherRepository>()

    private val testDispatcher = StandardTestDispatcher()

    private val testCity = City(
        id = 1001,
        name = "Seoul",
        nameKo = "서울",
        state = "",
        country = "KR",
        coord = Coord(lat = 37.56, lon = 126.97)
    )

    private val weatherResponse = WeatherResponse(
        coord = null,
        weather = listOf(WeatherDto(800, "Clear", "맑음", "01d")),
        main = MainDto(temp = 25.0),
        wind = null, clouds = null, rain = null, snow = null,
        dt = null, sys = null, timezone = null, id = null, name = null,
        cod = null, visibility = null, base = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { cityStorageRepo.getSavedCities() } returns listOf(testCity)
        coEvery {
            weatherRepo.getWeather(
                cityId = testCity.id,
                latitude = testCity.coord.lat,
                longitude = testCity.coord.lon
            )
        } returns weatherResponse

        viewModel = WeatherListViewModel(cityStorageRepo, weatherRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherForSavedCities should populate weatherList`() = runTest {
        viewModel.loadWeatherForSavedCities()
        advanceUntilIdle()

        val weatherList = viewModel.weatherList.value
        assertEquals(1, weatherList.size)
        val item = weatherList[0]
        assertEquals("서울", item.cityName)
        assertEquals(25, item.temperature)
        assertEquals("맑음", item.weatherType)
        assertEquals("01d", item.iconCode)
    }

    @Test
    fun `deleteSelected should remove cities from list and storage`() = runTest {
        // 초기 리스트 주입
        viewModel.loadWeatherForSavedCities()
        advanceUntilIdle()

        coEvery { cityStorageRepo.removeCities(any()) } just runs

        viewModel.deleteSelected(listOf(testCity))
        advanceUntilIdle()

        assertEquals(0, viewModel.weatherList.value.size)
        coVerify { cityStorageRepo.removeCities(withArg { assertEquals(1, it.size) }) }
    }

    @Test
    fun `moveItem should swap items correctly`() = runTest {
        val city2 = testCity.copy(id = 2002, name = "Busan", nameKo = "부산", coord = Coord(35.17, 129.07))
        val cityList = listOf(testCity, city2)

        coEvery { cityStorageRepo.getSavedCities() } returns cityList
        coEvery {
            weatherRepo.getWeather(any(), any(), any())
        } returns weatherResponse

        viewModel.loadWeatherForSavedCities()
        advanceUntilIdle()

        viewModel.moveItem(0, 1)

        val reordered = viewModel.weatherList.value
        assertEquals("부산", reordered[0].cityName)
        assertEquals("서울", reordered[1].cityName)
    }
}
