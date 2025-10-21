package com.example.weathernow.data.workers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weathernow.data.notifications.NotificationHelper
import com.example.weathernow.domain.model.WeatherInfo
import com.example.weathernow.domain.repository.WeatherRepository
import com.google.android.gms.location.LocationServices
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: WeatherRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    return Result.success()
                }
            }

            val weather = getUserWeather() ?: return Result.retry()

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
                        "Drink water and stay cool!"
                    )
                }

                weather.currentTemp < 5 -> {
                    notificationHelper.showWeatherNotification(
                        "❄️ Very cold weather",
                        "Wear warm clothes today!"
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getUserWeather(): WeatherInfo? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                repository.getWeatherByCoordinates(location.latitude, location.longitude)
            } else {
                repository.getWeatherByCity("Cairo") // fallback
            }
        } catch (e: Exception) {
            repository.getWeatherByCity("Cairo") // fallback
        }
    }
}
