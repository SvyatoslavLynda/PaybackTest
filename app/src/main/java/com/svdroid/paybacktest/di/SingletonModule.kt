package com.svdroid.paybacktest.di

import android.content.Context
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import androidx.room.Room
import com.svdroid.paybacktest.data.db.PixabayDatabase
import com.svdroid.paybacktest.data.api.PixabayService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideRetrofitInstance(): PixabayService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { level = HttpLoggingInterceptor.Level.BODY }
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl("https://pixabay.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PixabayService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): PixabayDatabase {
        return Room.databaseBuilder(
            appContext,
            PixabayDatabase::class.java,
            "pixabay"
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideNavHostController(@ApplicationContext appContext: Context): NavHostController {
        return NavHostController(appContext).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }
    }
}