package com.svdroid.paybacktest.di

import com.svdroid.paybacktest.data.db.PixabayDatabase
import com.svdroid.paybacktest.data.ImagesRepository
import com.svdroid.paybacktest.data.api.PixabayService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideNewsRepository(newsApiService: PixabayService, pixabayDatabase: PixabayDatabase): ImagesRepository =
        ImagesRepository(newsApiService, pixabayDatabase)
}