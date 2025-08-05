package com.ben.simpleweather.network

import com.ben.simpleweather.BuildConfig
import com.ben.simpleweather.data.remote.dto.ForecastResponse
import com.ben.simpleweather.data.remote.dto.WeatherResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class WeatherApiTest {

    private lateinit var weatherApi: WeatherApi
    private val seoulLatitude = 37.5665
    private val seoulLongitude = 126.9780

    @Before
    fun setUp() {
        if (BuildConfig.WEATHER_API_KEY.isEmpty()) {
            throw IllegalStateException("WEATHER_API_KEY in BuildConfig is empty. Ensure it's set in local.properties and Gradle sync is complete.")
        }

        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true // DTO의 기본값을 활용하기 위함
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val originalHttpUrl: HttpUrl = original.url

                val url: HttpUrl = originalHttpUrl.newBuilder()
                    .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
                    .build()

                val requestBuilder: Request.Builder = original.newBuilder()
                    .url(url)
                    .header("Content-Type", "application/json")

                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    @Test
    fun `getCurrentWeather for Seoul should return current weather data`() = runBlocking {
        // API 호출
        val response: WeatherResponse = weatherApi.getCurrentWeather(
            latitude = seoulLatitude,
            longitude = seoulLongitude
            // units와 lang은 WeatherApi 인터페이스에 정의된 기본값 사용
        )
        // 응답 검증
        assertNotNull("Response body should not be null", response)
        assertEquals("Response code (cod) should be 200", 200, response.cod)
        assertNotNull("Weather list should not be null", response.weather)
        assertTrue("Weather list should not be empty", response.weather?.isNotEmpty() == true)
        assertNotNull("Main data (temp, humidity etc.) should not be null", response.main)
        // API가 반환하는 도시 이름이 "Seoul"과 정확히 일치하는지 확인 (약간의 변동 가능성 있음)
        assertEquals("City name should be Seoul", "Seoul", response.name)
    }

    @Test
    fun `getWeatherForecast for Seoul should return forecast data`() = runBlocking {
        // API 호출
        val response: ForecastResponse = weatherApi.getWeatherForecast(
            latitude = seoulLatitude,
            longitude = seoulLongitude
            // units, lang, cnt는 WeatherApi 인터페이스에 정의된 기본값 또는 null 사용
        )
        println("Weather Forecast Response: $response") // 응답 내용 출력 추가

        // 응답 검증
        assertNotNull("Response body should not be null", response)
        assertEquals("Response code (cod) should be \"200\"", "200", response.cod) // Forecast API의 cod는 String 타입
        assertNotNull("Forecast list should not be null", response.list)
        assertTrue("Forecast list should not be empty", response.list?.isNotEmpty() == true)
        assertNotNull("City data should not be null", response.city)
        assertEquals("City name should be Seoul", "Seoul", response.city?.name)
    }
}
