package com.practice.watch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class TiltingActivity : AppCompatActivity(),SensorEventListener{

    private lateinit var sensorManager : SensorManager
    private lateinit var square : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tilting)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        square = findViewById(R.id.tv_square)

        setUpSensorStuff()
    }

    private fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this,
                it,SensorManager.SENSOR_DELAY_FASTEST,SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = event.values[0]
            val upDown = event.values[1]

            square.apply{
                rotationX = upDown*3f
                rotationY = sides*3f
                rotation = -sides
                translationX= sides* -10
                translationY = upDown * 10
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    /* override fun onSensorChanged(event: SensorEvent) {
         // In this example, alpha is calculated as t / (t + dT),
         // where t is the low-pass filter's time-constant and
         // dT is the event delivery rate.

         val alpha: Float = 0.8f

         // Isolate the force of gravity with the low-pass filter.
         gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
         gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
         gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

         // Remove the gravity contribution with the high-pass filter.
         linear_acceleration[0] = event.values[0] - gravity[0]
         linear_acceleration[1] = event.values[1] - gravity[1]
         linear_acceleration[2] = event.values[2] - gravity[2]
     }*/
}