package com.mvl.locationassignment.di

import com.mvl.locationassignment.data.repository.TripRepository
import com.mvl.locationassignment.domain.usecase.GetTripsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetTripsUseCase(
        tripRepository: TripRepository
    ): GetTripsUseCase {
        return GetTripsUseCase(tripRepository)
    }
}
