package com.svdroid.paybacktest.di

import com.svdroid.paybacktest.data.db.HitDao
import com.svdroid.paybacktest.data.db.PixabayDatabase
import com.svdroid.paybacktest.data.db.SearchSuggestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideHitDao(pixabayDatabase: PixabayDatabase): HitDao {
        return pixabayDatabase.hitDao()
    }

    @Provides
    fun provideSuggestionDao(pixabayDatabase: PixabayDatabase): SearchSuggestionDao {
        return pixabayDatabase.searchSuggestionDao()
    }
}