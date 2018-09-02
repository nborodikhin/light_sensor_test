package com.pinnacleimagingsystems.lightsensortest

import android.arch.lifecycle.*
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

enum class SensorPresence {
    UNKNOWN,
    ABSENT,
    PRESENT,
    CONNECTED
}

enum class SensorAccuracy {
    UNKNOWN,
    NO_CONTACT,
    UNRELIABLE,
    LOW,
    MEDIUM,
    HIGH
}

class MainViewModel: ViewModel(), LifecycleObserver {
    val sensorPresence = MutableLiveData<SensorPresence>().apply { value = SensorPresence.UNKNOWN }
    val sensorValue = MutableLiveData<Float>().apply { value = -1f }
    val sensorAccuracy = MutableLiveData<SensorAccuracy>().apply { value = SensorAccuracy.UNKNOWN }
    val sensorResolution = MutableLiveData<Float>()

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    fun initSensor(context: Context) {
        if (sensorPresence.value != SensorPresence.UNKNOWN) return

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorPresence.value = if (sensor != null) SensorPresence.PRESENT else SensorPresence.ABSENT
        sensorResolution.value = sensor?.resolution ?: 0.0f
    }

    private val lightEventListener = object: SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            sensorAccuracy.postValue(when(accuracy) {
                SensorManager.SENSOR_STATUS_NO_CONTACT -> SensorAccuracy.NO_CONTACT
                SensorManager.SENSOR_STATUS_UNRELIABLE -> SensorAccuracy.UNRELIABLE
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.LOW
                SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.MEDIUM
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.HIGH
                else -> SensorAccuracy.UNKNOWN
            })
        }

        override fun onSensorChanged(event: SensorEvent) {
            sensorValue.postValue(event.values[0])
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startSensor() {
        if (sensor != null) {
            sensorManager.registerListener(lightEventListener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopSensor() {
        sensorManager.unregisterListener(lightEventListener)
    }
}


