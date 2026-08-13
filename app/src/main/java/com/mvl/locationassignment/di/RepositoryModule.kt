package com.mvl.locationassignment.di

import android.content.Context
import com.mvl.locationassignment.data.datasource.AqiDataSource
import com.mvl.locationassignment.data.datasource.AqiDataSourceImpl
import com.mvl.locationassignment.data.datasource.LocationDataSource
import com.mvl.locationassignment.data.datasource.LocationDataSourceImpl
import com.mvl.locationassignment.data.repository.AqiRepositoryImpl
import com.mvl.locationassignment.data.repository.LocationRepositoryImpl
import com.mvl.locationassignment.domain.repository.AqiRepository
import com.mvl.locationassignment.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindAqiRepository(
        impl: AqiRepositoryImpl
    ): AqiRepository

    @Binds
    @Singleton
    abstract fun bindAqiDataSource(
        impl: AqiDataSourceImpl
    ): AqiDataSource
    
    companion object {
        @Provides
        @Singleton
        fun provideLocationDataSource(
            @ApplicationContext context: Context
        ): LocationDataSource {
            return LocationDataSourceImpl(context)
        }
    }
}
