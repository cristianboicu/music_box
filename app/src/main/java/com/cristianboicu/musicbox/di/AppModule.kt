package com.cristianboicu.musicbox.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.cristianboicu.musicbox.data.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicRepository(@ApplicationContext context: Context) = MusicRepository(context)

}