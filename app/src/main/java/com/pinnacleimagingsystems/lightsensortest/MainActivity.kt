package com.pinnacleimagingsystems.lightsensortest

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this)[MainViewModel::class.java].apply {
            initSensor(applicationContext)
        }
    }

    private val views by lazy { object {
        val presence: TextView = findViewById(R.id.presence)
        val accuracy: TextView = findViewById(R.id.accuracy)
        val resolution: TextView = findViewById(R.id.resolution)
        val value: TextView = findViewById(R.id.value)
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(viewModel)

        viewModel.run {
            sensorValue.observe({ lifecycle }) { value -> onLightValueUpdated(value!!) }
            sensorPresence.observe({ lifecycle}) { value -> onSensorPresenceChanged(value!!)}
            sensorAccuracy.observe({ lifecycle }) { value -> onSensorAccuracyChanged(value!!)}
            sensorResolution.observe({ lifecycle }) { value -> onSensorResultionChanged(value!!) }
        }
    }

    fun onSensorPresenceChanged(presence: SensorPresence) {
        views.presence.text = "Presence: $presence"
    }

    fun onSensorAccuracyChanged(accuracy: SensorAccuracy) {
        views.accuracy.text = "Accuracy: $accuracy"
    }

    fun onSensorResultionChanged(resolution: Float) {
        views.resolution.text = "Resolution: $resolution"
    }

    fun onLightValueUpdated(value: Float) {
        views.value.text = "Value: $value"
    }
}
