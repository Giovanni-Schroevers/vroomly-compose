package com.fsa_profgroep_4.vroomly.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.fsa_profgroep_4.vroomly.R
import com.fsa_profgroep_4.vroomly.data.drive.DriveRepository
import com.example.rocketreserver.type.LocationSnapshotInput
import com.google.android.gms.location.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class LocationService : Service(), KoinComponent, SensorEventListener {
    
    private val driveRepository: DriveRepository by inject()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    companion object {
        const val CHANNEL_ID = "location_tracking_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val snapshot = LocationSnapshotInput(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = location.time
                    )
                    driveRepository.addSnapshot(snapshot)
                }
            }
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.vroomly_drive_tracking))
            .setContentText(getString(R.string.tracking_your_drive_in_the_background))
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
            
        driveRepository.startDrive()

        startForeground(NOTIFICATION_ID, notification)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(10))
            .setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(10))
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopTracking() {
        driveRepository.stopDrive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            val magnitude = sqrt((x * x + y * y + z * z).toDouble())
            driveRepository.updateMaxAcceleration(magnitude)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null
}
