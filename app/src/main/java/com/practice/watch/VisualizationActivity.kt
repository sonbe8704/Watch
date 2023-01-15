package com.practice.watch

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

class VisualizationActivity : AppCompatActivity(),SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var timedelta : ArrayList<Long> = arrayListOf()
    var AXdata : ArrayList<Double> = arrayListOf()
    var AYdata : ArrayList<Double> = arrayListOf()
    var AZdata : ArrayList<Double> = arrayListOf()
    var VXdata : ArrayList<Double> = arrayListOf()
    var VYdata : ArrayList<Double> = arrayListOf()
    var VZdata : ArrayList<Double> = arrayListOf()
    var PXdata : ArrayList<Double> = arrayListOf()
    var PYdata : ArrayList<Double> = arrayListOf()
    var PZdata : ArrayList<Double> = arrayListOf()

    var AXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var AYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var AZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var VXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var VYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var VZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var PXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var PYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var PZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )



    private lateinit var Aviewport: Viewport
    private lateinit var Vviewport: Viewport
    private lateinit var Pviewport: Viewport

    @OptIn(ExperimentalTime::class)
    private var mark : TimeMark?= null
    var pointsPlotted: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualization)
        setUpGraph()
        setUpButton()
    }

    private fun setUpGraph(){
        val Agraph: GraphView = findViewById(R.id.Agraph)

        Aviewport = Agraph.viewport
        Aviewport.isScrollable = true
        Aviewport.isXAxisBoundsManual = true
        AXseries.color= Color.RED
        AYseries.color = Color.BLUE
        AZseries.color = Color.GREEN
        Agraph.addSeries(AXseries)
        Agraph.addSeries(AYseries)
        Agraph.addSeries(AZseries)

        val Vgraph: GraphView = findViewById(R.id.Vgraph)
        Vviewport = Vgraph.viewport
        Vviewport.isScrollable = true
        Vviewport.isXAxisBoundsManual = true
        VXseries.color= Color.RED
        VYseries.color = Color.BLUE
        VZseries.color = Color.GREEN
        Vgraph.addSeries(VXseries)
        Vgraph.addSeries(VYseries)
        Vgraph.addSeries(VZseries)

        val Pgraph: GraphView = findViewById(R.id.Pgraph)

        Pviewport = Pgraph.viewport
        Pviewport.isScrollable = true
        Pviewport.isXAxisBoundsManual = true
        PXseries.color= Color.RED
        PYseries.color = Color.BLUE
        PZseries.color = Color.GREEN
        Pgraph.addSeries(PXseries)
        Pgraph.addSeries(PYseries)
        Pgraph.addSeries(PZseries)

    }

    private fun setUpButton(){
        //시작버튼 누르면 가속도 그래프 출력
        val end : TextView = findViewById(R.id.btn_end)
        val start :TextView = findViewById(R.id.btn_start)
        end.setOnClickListener(View.OnClickListener {
            sensorManager.unregisterListener(this)

        })
        start.setOnClickListener(View.OnClickListener {
            //그래프 초기화

            AXdata= arrayListOf()
            AYdata= arrayListOf()
            AZdata= arrayListOf()
            VXdata= arrayListOf()
            VYdata= arrayListOf()
            VZdata= arrayListOf()
            PXdata= arrayListOf()
            PYdata= arrayListOf()
            PZdata= arrayListOf()


            AXseries.resetData(arrayOf())
            AYseries.resetData(arrayOf())
            AZseries.resetData(arrayOf())
            VXseries.resetData(arrayOf())
            VYseries.resetData(arrayOf())
            VZseries.resetData(arrayOf())
            PXseries.resetData(arrayOf())
            PYseries.resetData(arrayOf())
            PZseries.resetData(arrayOf())

            timedelta= arrayListOf()
            // 가속도 센서 시작
            setUpSensorStuff()
            // 가속도 그래프 표시
        })

    }

    private fun getVelocity(){

    }

    private fun setUpSensorStuff() {
        // 위치 나타내기 초기화
        pointsPlotted=0.0


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

    @OptIn(ExperimentalTime::class)
    override fun onSensorChanged(event: SensorEvent?) {
        //타임스탬프와 함께 가속도 저장
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x: Double = event.values[0].toDouble() * 3f
            val y: Double = event.values[1].toDouble() * 3f
            val z: Double = event.values[2].toDouble() * 3f


            pointsPlotted++;
            // 데이터 저장
            AXdata.add(x)
            AYdata.add(y)
            AZdata.add(z)
            // 그래프용으로 저장
            AXseries.appendData(DataPoint(pointsPlotted, x), true, pointsPlotted.toInt())
            AYseries.appendData(DataPoint(pointsPlotted, y), true, pointsPlotted.toInt())
            AZseries.appendData(DataPoint(pointsPlotted, z), true, pointsPlotted.toInt())
            //시간 저장
            timedelta.add(System.currentTimeMillis())


            Log.e("DataPlotted : ",pointsPlotted.toInt().toString())
            if(AXdata.size>1){
                var Aidx= AXdata.size-1
                var Vidx=AXdata.size-2

                var delta = timedelta[Aidx] - timedelta[Aidx - 1]
                Log.e("delta : ", delta.toString())
                var currVX  = delta * (AXdata[Aidx-1] + AXdata[Aidx]) / 2
                var currVY = delta*(AYdata[Aidx-1]+AYdata[Aidx])/2
                var currVZ = delta*(AZdata[Aidx-1]+AZdata[Aidx])/2
                if(Vidx==0) {
                    VXdata.add(currVX)
                    VYdata.add(currVY)
                    VZdata.add(currVZ)
                }
                else {
                    VXdata.add(VXdata[Vidx - 1] + currVX)
                    VYdata.add(VYdata[Vidx - 1] + currVY)
                    VZdata.add(VZdata[Vidx - 1] + currVZ)
                }
                VXseries.appendData(DataPoint(pointsPlotted, VXdata[Vidx]), true, pointsPlotted.toInt())
                VYseries.appendData(DataPoint(pointsPlotted, VYdata[Vidx]), true, pointsPlotted.toInt())
                VZseries.appendData(DataPoint(pointsPlotted, VZdata[Vidx]), true, pointsPlotted.toInt())
            }

            if(VXdata.size>1){
                var Aidx = AXdata.size-1
                var Vidx = Aidx-1
                var Pidx = Vidx-1
                var delta = timedelta[Vidx] - timedelta[Vidx - 1]
                var currPX  = delta * (VXdata[Vidx-1] + VXdata[Vidx]) / 2
                var currPY = delta*(VYdata[Vidx-1]+VYdata[Vidx])/2
                var currPZ = delta*(VZdata[Vidx-1]+VZdata[Vidx])/2
                if(Pidx==0){
                    PXdata.add(currPX)
                    PYdata.add(currPY)
                    PZdata.add(currPZ)
                }
                else{
                    PXdata.add(PXdata[Pidx-1]+currPX)
                    PYdata.add(PYdata[Pidx-1]+currPY)
                    PZdata.add(PZdata[Pidx-1]+currPZ)
                }

                PXseries.appendData(DataPoint(pointsPlotted, PXdata[Pidx]), true, pointsPlotted.toInt())
                PYseries.appendData(DataPoint(pointsPlotted, PYdata[Pidx]), true, pointsPlotted.toInt())
                PZseries.appendData(DataPoint(pointsPlotted, PZdata[Pidx]), true, pointsPlotted.toInt())
            }
            Aviewport.setMaxX(pointsPlotted);
            Aviewport.setMinX(pointsPlotted - 200)
            Vviewport.setMaxX(pointsPlotted);
            Vviewport.setMinX(pointsPlotted - 200)
            Pviewport.setMaxX(pointsPlotted);
            Pviewport.setMinX(pointsPlotted - 200)


        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}