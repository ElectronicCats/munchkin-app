package com.example.munchkin_app.ui.di

import android.content.Context
import com.example.munchkin_app.UsbHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUsbHelper (
        @ApplicationContext context: Context
    ): UsbHelper {
        return UsbHelper(context)
    }



}
