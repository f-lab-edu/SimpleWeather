package com.ben.simpleweather

import android.content.Context
import com.ben.simpleweather.data.City
import com.ben.simpleweather.data.Coord
import com.ben.simpleweather.data.repository.CitySearchRepository
import com.ben.simpleweather.data.repository.CityStorageRepository
import com.ben.simpleweather.features.search.CitySearchViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CitySearchViewModelTest {

    private lateinit var viewModel: CitySearchViewModel

    private val fakeCitySearchRepo = mockk<CitySearchRepository>()
    private val fakeCityStorageRepo = mockk<CityStorageRepository>()
    private val context = mockk<Context>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { fakeCitySearchRepo.searchCities(any()) } returns emptyList()

        every { context.getString(any()) } answers {
            when (firstArg<Int>()) {
                R.string.city_add_duplicate -> "이미 등록된 도시입니다."
                R.string.city_add_limit -> "도시는 최대 10개까지 저장할 수 있습니다."
                R.string.city_add_success -> "도시가 성공적으로 추가되었습니다."
                else -> "알 수 없는 메시지"
            }
        }

        viewModel = CitySearchViewModel(
            citySearchRepository = fakeCitySearchRepo,
            cityStorageRepository = fakeCityStorageRepo,
            context = context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChanged should update searchResult`() = runTest {
        val dummyList = listOf(City(1, "Seoul", "서울", "", "KR", Coord(126.97, 37.56)))
        coEvery { fakeCitySearchRepo.searchCities("Seoul") } returns dummyList

        viewModel.onQueryChanged("Seoul")
        advanceUntilIdle()

        assertEquals(dummyList, viewModel.searchResult.value)
    }

    @Test
    fun `addCity should show duplicate message if city already exists`() = runTest {
        val city = City(1, "Seoul", "서울", "", "KR", Coord(126.97, 37.56))
        coEvery { fakeCityStorageRepo.getSavedCities() } returns listOf(city)
        every { context.getString(R.string.city_add_duplicate) } returns "이미 등록된 도시입니다."

        viewModel.addCity(city)
        advanceUntilIdle()

        assertEquals("이미 등록된 도시입니다.", viewModel.message.value)
    }

    @Test
    fun `addCity should show limit message if 10 cities already saved`() = runTest {
        val inputCity = City(100, "Busan", "부산", "", "KR", Coord(129.07, 35.17))
        val dummy = City(1, "Seoul", "서울", "", "KR", Coord(126.97, 37.56))

        // dummy.id != inputCity.id 로 중복 제거
        coEvery { fakeCityStorageRepo.getSavedCities() } returns List(10) { index ->
            dummy.copy(id = index + 1) // id: 1~10
        }

        viewModel.addCity(inputCity)  // id: 100
        advanceUntilIdle()

        assertEquals("도시는 최대 10개까지 저장할 수 있습니다.", viewModel.message.value)
    }

    @Test
    fun `addCity should add city and show success message`() = runTest {
        val city = City(1, "Busan", "부산", "", "KR", Coord(129.07, 35.17))
        coEvery { fakeCityStorageRepo.getSavedCities() } returns emptyList()
        coEvery { fakeCityStorageRepo.addCity(city) } just Runs
        every { context.getString(R.string.city_add_success) } returns "도시가 성공적으로 추가되었습니다."

        viewModel.addCity(city)
        advanceUntilIdle()

        assertEquals("도시가 성공적으로 추가되었습니다.", viewModel.message.value)
    }
}
