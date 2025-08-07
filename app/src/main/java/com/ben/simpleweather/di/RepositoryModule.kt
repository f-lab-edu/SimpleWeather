package com.ben.simpleweather.di

import com.ben.simpleweather.data.repository.CitySearchRepository
import com.ben.simpleweather.data.repository.CitySearchRepositoryImpl
import com.ben.simpleweather.data.repository.CityStorageRepository
import com.ben.simpleweather.data.repository.CityStorageRepositoryImpl
import com.ben.simpleweather.data.repository.WeatherRepository
import com.ben.simpleweather.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCitySearchRepository(
        impl: CitySearchRepositoryImpl
    ): CitySearchRepository

    @Binds
    @Singleton
    abstract fun bindCityStorageRepository(
        impl: CityStorageRepositoryImpl
    ): CityStorageRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}