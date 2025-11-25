package com.example.munchkin_app.di

import android.content.Context
import com.example.munchkin_app.data.datastore.SsidSpamConfigDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideSsidSpamConfigDataStore(@ApplicationContext context: Context): SsidSpamConfigDataStore {
        return SsidSpamConfigDataStore(context)
    }
}