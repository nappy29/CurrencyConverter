package com.example.paypaycodechallenge.di.module

import android.content.Context
import androidx.room.Room
import com.example.paypaycodechallenge.BuildConfig
import com.example.paypaycodechallenge.data.local.dao.CurrencyDao
import com.example.paypaycodechallenge.data.local.db.CurrencyDatabase
import com.example.paypaycodechallenge.data.remote.ApiService
import com.example.paypaycodechallenge.data.repo.RepositoryHelper
import com.example.paypaycodechallenge.data.repo.RepositoryImpl
import com.example.paypaycodechallenge.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {



    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            appContext,
            CurrencyDatabase::class.java,
            "RssReader"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(appDatabase: CurrencyDatabase): CurrencyDao {
        return appDatabase.getCurrencyDao()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: ApiService, currencyDao: CurrencyDao): RepositoryHelper = RepositoryImpl(api, currencyDao)

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext appContext: Context): NetworkHelper = NetworkHelper(appContext)
}