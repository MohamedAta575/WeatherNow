package com.example.weathernow.di

import android.app.Application
import com.example.weathernow.data.notifications.NotificationHelper
import com.example.weathernow.domain.use_case.SendWeatherNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotificationHelper(app: Application): NotificationHelper {
        val helper = NotificationHelper(app)
        helper.createNotificationChannel()
        return helper
    }

    @Provides
    @Singleton
    fun provideSendWeatherNotificationUseCase(
        notificationHelper: NotificationHelper
    ): SendWeatherNotificationUseCase {
        return SendWeatherNotificationUseCase(notificationHelper)
    }
}
