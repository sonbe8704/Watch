package com.practice.watch

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class GraphActivity : AppCompatActivity(),SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private lateinit var Xviewport: Viewport
    private lateinit var Yviewport: Viewport
    private lateinit var Zviewport: Viewport
    val Xseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    val Yseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    val Zseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var pointsPlotted: Double = 0.0
    var graphIntervalCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setUpGraph()
        setUpSensorStuff()
/*
        var data : Array<DataPoint> = arrayOf(DataPoint(0.0,1.0),
            DataPoint(1.0,5.0),
            DataPoint(2.0,3.0),
            DataPoint(3.0,2.0),
            DataPoint(4.0,6.0))
*/

    }

    private fun setUpGraph(){
        val Xgraph: GraphView = findViewById(R.id.Xgraph)

        Xviewport = Xgraph.viewport
        Xviewport.isScrollable = true
        Xviewport.isXAxisBoundsManual = true
        Xgraph.addSeries(Xseries)

        val Ygraph: GraphView = findViewById(R.id.Ygraph)

        Yviewport = Ygraph.viewport
        Yviewport.isScrollable = true
        Yviewport.isXAxisBoundsManual = true
        Ygraph.addSeries(Yseries)

        val Zgraph: GraphView = findViewById(R.id.Zgraph)

        Zviewport = Zgraph.viewport
        Zviewport.isScrollable = true
        Zviewport.isXAxisBoundsManual = true
        Zgraph.addSeries(Zseries)
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //LINEAR_ACCELEROMETER 중력제거
        // ACCELEROMETER 중력포함
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x: Double = event.values[0].toDouble() * 3f
            val y: Double = event.values[1].toDouble() * 3f
            val z: Double = event.values[2].toDouble() * 3f

            pointsPlotted++;

            Xseries.appendData(DataPoint(pointsPlotted, x), true, pointsPlotted.toInt())
            Xviewport.setMaxX(pointsPlotted);
            Xviewport.setMinX(pointsPlotted - 100)

            Yseries.appendData(DataPoint(pointsPlotted, y), true, pointsPlotted.toInt())
            Yviewport.setMaxX(pointsPlotted);
            Yviewport.setMinX(pointsPlotted - 100)

            Zseries.appendData(DataPoint(pointsPlotted, z), true, pointsPlotted.toInt())
            Zviewport.setMaxX(pointsPlotted);
            Zviewport.setMinX(pointsPlotted - 100)
            Log.e("new XData", x.toString())
            Log.e("new YData", y.toString())
            Log.e("new ZData", z.toString())

            if (pointsPlotted > 1000) {
                pointsPlotted = 0.0;
                Xseries.resetData(arrayOf())
                Yseries.resetData(arrayOf())
                Zseries.resetData(arrayOf())
            }

        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}

