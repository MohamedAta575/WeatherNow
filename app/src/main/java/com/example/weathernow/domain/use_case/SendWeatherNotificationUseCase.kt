package com.example.weathernow.domain.use_case

import com.example.weathernow.data.notifications.NotificationHelper
import com.example.weathernow.domain.model.WeatherInfo
import javax.inject.Inject

class SendWeatherNotificationUseCase @Inject constructor(
    private val notificationHelper: NotificationHelper
) {
    operator fun invoke(weather: WeatherInfo) {
        when {
            weather.condition.contains("rain", ignoreCase = true) -> {
                notificationHelper.showWeatherNotification(
                    "🌧 Chance of rain",
                    "Carry an umbrella today!"
                )
            }
            weather.currentTemp > 35 -> {
                notificationHelper.showWeatherNotification(
                    "🔥 It's hot outside",
                    "Drink plenty of water and stay cool!"
                )
            }
            weather.currentTemp < 5 -> {
                notificationHelper.showWeatherNotification(
                    "❄️ Very cold weather",
                    "Wear warm clothes today!"
                )
            }
        }
    }
}
