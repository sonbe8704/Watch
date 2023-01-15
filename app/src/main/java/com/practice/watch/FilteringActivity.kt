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

class FilteringActivity : AppCompatActivity(),SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var timedelta : ArrayList<Long> = arrayListOf()
    var AXdata : ArrayList<Double> = arrayListOf()
    var AYdata : ArrayList<Double> = arrayListOf()
    var AZdata : ArrayList<Double> = arrayListOf()

    var F_AXdata : ArrayList<Double> = arrayListOf()
    var F_AYdata : ArrayList<Double> = arrayListOf()
    var F_AZdata : ArrayList<Double> = arrayListOf()
    var F_VXdata : ArrayList<Double> = arrayListOf()
    var F_VYdata : ArrayList<Double> = arrayListOf()
    var F_VZdata : ArrayList<Double> = arrayListOf()
    var F_PXdata : ArrayList<Double> = arrayListOf()
    var F_PYdata : ArrayList<Double> = arrayListOf()
    var F_PZdata : ArrayList<Double> = arrayListOf()

    var F_AXWindow : Double= 0.0
    var F_AYWindow : Double= 0.0
    var F_AZWindow : Double= 0.0

    var F_AXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_AYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_AZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_VXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_VYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_VZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_PXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_PYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf()
    )
    var F_PZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
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
        setContentView(R.layout.activity_filtering)
        setUpGraph()
        setUpButton()
    }

    private fun setUpGraph(){
        val Agraph: GraphView = findViewById(R.id.Agraph)

        Aviewport = Agraph.viewport
        Aviewport.isScrollable = true
        Aviewport.isXAxisBoundsManual = true
        F_AXseries.color= Color.RED
        F_AYseries.color = Color.BLUE
        F_AZseries.color = Color.GREEN
        Agraph.addSeries(F_AXseries)
        Agraph.addSeries(F_AYseries)
        Agraph.addSeries(F_AZseries)

        val Vgraph: GraphView = findViewById(R.id.Vgraph)
        Vviewport = Vgraph.viewport
        Vviewport.isScrollable = true
        Vviewport.isXAxisBoundsManual = true
        F_VXseries.color= Color.RED
        F_VYseries.color = Color.BLUE
        F_VZseries.color = Color.GREEN
        Vgraph.addSeries(F_VXseries)
        Vgraph.addSeries(F_VYseries)
        Vgraph.addSeries(F_VZseries)

        val Pgraph: GraphView = findViewById(R.id.Pgraph)

        Pviewport = Pgraph.viewport
        Pviewport.isScrollable = true
        Pviewport.isXAxisBoundsManual = true
        F_PXseries.color= Color.RED
        F_PYseries.color = Color.BLUE
        F_PZseries.color = Color.GREEN
        Pgraph.addSeries(F_PXseries)
        Pgraph.addSeries(F_PYseries)
        Pgraph.addSeries(F_PZseries)

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


            F_AXdata= arrayListOf()
            F_AYdata= arrayListOf()
            F_AZdata= arrayListOf()
            F_VXdata= arrayListOf()
            F_VYdata= arrayListOf()
            F_VZdata= arrayListOf()
            F_PXdata= arrayListOf()
            F_PYdata= arrayListOf()
            F_PZdata= arrayListOf()

            F_AXseries.resetData(arrayOf())
            F_AYseries.resetData(arrayOf())
            F_AZseries.resetData(arrayOf())
            F_VXseries.resetData(arrayOf())
            F_VYseries.resetData(arrayOf())
            F_VZseries.resetData(arrayOf())
            F_PXseries.resetData(arrayOf())
            F_PYseries.resetData(arrayOf())
            F_PZseries.resetData(arrayOf())

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
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME
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
            /*if(x<0.3)x=0.0
            if(y<0.3)y=0.0
            if(z<0.3)z=0.0*/

            pointsPlotted++;
            // 데이터 저장
            AXdata.add(x)
            AYdata.add(y)
            AZdata.add(z)
            // 그래프용으로 저장
            //시간 저장
            timedelta.add(System.currentTimeMillis())
            // 필터링 데이터 저장
            if(AXdata.size<20){
                F_AXWindow=F_AXWindow+x
                F_AYWindow=F_AYWindow+y
                F_AZWindow=F_AZWindow+z
            }
            else if(AXdata.size==20){
                F_AXdata.add(x-F_AXWindow/20)
                F_AYdata.add(y-F_AYWindow/20)
                F_AZdata.add(z-F_AZWindow/20)
                Log.e("TAG",F_AXdata[F_AXdata.size-1].toString())
                Log.e("TAG",F_AYdata[F_AYdata.size-1].toString())
                Log.e("TAG",F_AZdata[F_AZdata.size-1].toString())
                F_AXseries.appendData(DataPoint(pointsPlotted, F_AXdata[F_AXdata.size-1]), true, pointsPlotted.toInt())
                F_AYseries.appendData(DataPoint(pointsPlotted, F_AYdata[F_AYdata.size-1]), true, pointsPlotted.toInt())
                F_AZseries.appendData(DataPoint(pointsPlotted, F_AZdata[F_AZdata.size-1]), true, pointsPlotted.toInt())

            }
            else{
                F_AXWindow=F_AXWindow+x-AXdata[AXdata.size-21]
                F_AYWindow=F_AYWindow+y-AYdata[AYdata.size-21]
                F_AZWindow=F_AZWindow+z-AZdata[AZdata.size-21]
                F_AXdata.add(x-F_AXWindow/20)
                F_AYdata.add(y-F_AYWindow/20)
                F_AZdata.add(z-F_AZWindow/20)
                F_AXseries.appendData(DataPoint(pointsPlotted, F_AXdata[F_AXdata.size-1]), true, pointsPlotted.toInt())
                F_AYseries.appendData(DataPoint(pointsPlotted, F_AYdata[F_AYdata.size-1]), true, pointsPlotted.toInt())
                F_AZseries.appendData(DataPoint(pointsPlotted, F_AZdata[F_AZdata.size-1]), true, pointsPlotted.toInt())
            }

            Log.e("DataPlotted : ",pointsPlotted.toInt().toString())

            if(F_AXdata.size>1){
                var F_Aidx= F_AXdata.size-1
                var F_Vidx= F_AXdata.size-2

                var delta = (timedelta[F_Aidx] - timedelta[F_Aidx - 1])/1000.0
                Log.e("delta : ", delta.toString())
                var currVX  = delta * (F_AXdata[F_Aidx-1] + F_AXdata[F_Aidx]) / 2
                var currVY = delta*(F_AYdata[F_Aidx-1]+F_AYdata[F_Aidx])/2
                var currVZ = delta*(F_AZdata[F_Aidx-1]+F_AZdata[F_Aidx])/2

                if(F_Vidx==0) {
                    F_VXdata.add(currVX)
                    F_VYdata.add(currVY)
                    F_VZdata.add(currVZ)
                }
                else {
                    F_VXdata.add(F_VXdata[F_Vidx - 1] + currVX)
                    F_VYdata.add(F_VYdata[F_Vidx - 1] + currVY)
                    F_VZdata.add(F_VZdata[F_Vidx - 1] + currVZ)
                }
                F_VXseries.appendData(DataPoint(pointsPlotted, F_VXdata[F_Vidx]), true, pointsPlotted.toInt())
                F_VYseries.appendData(DataPoint(pointsPlotted, F_VYdata[F_Vidx]), true, pointsPlotted.toInt())
                F_VZseries.appendData(DataPoint(pointsPlotted, F_VZdata[F_Vidx]), true, pointsPlotted.toInt())
            }

            if(F_VXdata.size>1){
                var Aidx = F_AXdata.size-1
                var Vidx = Aidx-1
                var Pidx = Vidx-1
                var delta = timedelta[Vidx] - timedelta[Vidx - 1]
                var currPX  = delta * (F_VXdata[Vidx-1] + F_VXdata[Vidx]) / 2
                var currPY = delta*(F_VYdata[Vidx-1]+F_VYdata[Vidx])/2
                var currPZ = delta*(F_VZdata[Vidx-1]+F_VZdata[Vidx])/2
                if(Pidx==0){
                    F_PXdata.add(currPX)
                    F_PYdata.add(currPY)
                    F_PZdata.add(currPZ)
                }
                else{
                    F_PXdata.add(F_PXdata[Pidx-1]+currPX)
                    F_PYdata.add(F_PYdata[Pidx-1]+currPY)
                    F_PZdata.add(F_PZdata[Pidx-1]+currPZ)
                }

                F_PXseries.appendData(DataPoint(pointsPlotted, F_PXdata[Pidx]), true, pointsPlotted.toInt())
                F_PYseries.appendData(DataPoint(pointsPlotted, F_PYdata[Pidx]), true, pointsPlotted.toInt())
                F_PZseries.appendData(DataPoint(pointsPlotted, F_PZdata[Pidx]), true, pointsPlotted.toInt())
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