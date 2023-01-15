package com.practice.watch

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.abs
import kotlin.math.sqrt


class MotionDriftActivity : AppCompatActivity(),SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private lateinit var Aviewport: Viewport
    private lateinit var Vviewport: Viewport
    private lateinit var Pviewport: Viewport
    private var sensor: Sensor? = null
    private var Agraph: GraphView ?=null
    private var Vgraph: GraphView ?=null
    private var Pgraph : GraphView ?=null

    private var timedelta : ArrayList<Long> = arrayListOf()
    private var ison : Boolean = false
    private var startT : Long = -999
    var AXdata : ArrayList<Double> = arrayListOf()
    var AYdata : ArrayList<Double> = arrayListOf()
    var AZdata : ArrayList<Double> = arrayListOf()

    var VXdata : ArrayList<Double> = arrayListOf()
    var VYdata : ArrayList<Double> = arrayListOf()
    var VZdata : ArrayList<Double> = arrayListOf()

    var PXdata : ArrayList<Double> = arrayListOf()
    var PYdata : ArrayList<Double> = arrayListOf()
    var PZdata : ArrayList<Double> = arrayListOf()

    var OL_AXdata_index : ArrayList<Int> = arrayListOf()
    var OL_AYdata_index : ArrayList<Int> = arrayListOf()
    var OL_AZdata_index : ArrayList<Int> = arrayListOf()

    var Good_AXdata : ArrayList<Double> = arrayListOf()
    var Good_AYdata : ArrayList<Double> = arrayListOf()
    var Good_AZdata : ArrayList<Double> = arrayListOf()

    var Good_VXdata : ArrayList<Double> = arrayListOf()
    var Good_VYdata : ArrayList<Double> = arrayListOf()
    var Good_VZdata : ArrayList<Double> = arrayListOf()

    var Good_PXdata : ArrayList<Double> = arrayListOf()
    var Good_PYdata : ArrayList<Double> = arrayListOf()
    var Good_PZdata : ArrayList<Double> = arrayListOf()

    var Smooth_AXdata : ArrayList<Double> = arrayListOf()
    var Smooth_AYdata : ArrayList<Double> = arrayListOf()
    var Smooth_AZdata : ArrayList<Double> = arrayListOf()

    var Smooth_VXdata : ArrayList<Double> = arrayListOf()
    var Smooth_VYdata : ArrayList<Double> = arrayListOf()
    var Smooth_VZdata : ArrayList<Double> = arrayListOf()

    var Smooth_PXdata : ArrayList<Double> = arrayListOf()
    var Smooth_PYdata : ArrayList<Double> = arrayListOf()
    var Smooth_PZdata : ArrayList<Double> = arrayListOf()


    var AXseries: LineGraphSeries<DataPoint>?=null
    var AYseries: LineGraphSeries<DataPoint>?=null
    var AZseries: LineGraphSeries<DataPoint> ?=null

    var VXseries: LineGraphSeries<DataPoint>?=null
    var VYseries: LineGraphSeries<DataPoint>?=null
    var VZseries: LineGraphSeries<DataPoint> ?=null

    var PXseries: LineGraphSeries<DataPoint>?=null
    var PYseries: LineGraphSeries<DataPoint>?=null
    var PZseries: LineGraphSeries<DataPoint> ?=null

    var Good_AXseries: LineGraphSeries<DataPoint>?=null
    var Good_AYseries: LineGraphSeries<DataPoint>?=null
    var Good_AZseries: LineGraphSeries<DataPoint>?=null
    
    var Good_VXseries: LineGraphSeries<DataPoint>?=null
    var Good_VYseries: LineGraphSeries<DataPoint>?=null
    var Good_VZseries: LineGraphSeries<DataPoint>?=null

    var Good_PXseries: LineGraphSeries<DataPoint>?=null
    var Good_PYseries: LineGraphSeries<DataPoint>?=null
    var Good_PZseries: LineGraphSeries<DataPoint>?=null

    var Smooth_AXseries: LineGraphSeries<DataPoint>?=null
    var Smooth_AYseries: LineGraphSeries<DataPoint>?=null
    var Smooth_AZseries: LineGraphSeries<DataPoint>?=null

    var Smooth_VXseries: LineGraphSeries<DataPoint>?=null
    var Smooth_VYseries: LineGraphSeries<DataPoint>?=null
    var Smooth_VZseries: LineGraphSeries<DataPoint>?=null

    var Smooth_PXseries: LineGraphSeries<DataPoint>?=null
    var Smooth_PYseries: LineGraphSeries<DataPoint>?=null
    var Smooth_PZseries: LineGraphSeries<DataPoint>?=null

    var pointsPlotted: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motion_drift)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Agraph= findViewById(R.id.Agraph)
        Vgraph= findViewById(R.id.Vgraph)
        Pgraph = findViewById(R.id.Pgraph)

/*
        var data : Array<DataPoint> = arrayOf(DataPoint(0.0,1.0),
            DataPoint(1.0,5.0),
            DataPoint(2.0,3.0),
            DataPoint(3.0,2.0),
            DataPoint(4.0,6.0))
*/
        var btnStart : TextView = findViewById(R.id.btn_start)
        var btnEnd : TextView = findViewById(R.id.btn_end)
        btnStart.setOnClickListener {
            ison=false

            timedelta= arrayListOf()


            AXdata= arrayListOf()
            AYdata= arrayListOf()
            AZdata= arrayListOf()

            Good_AXdata= arrayListOf()
            Good_AYdata= arrayListOf()
            Good_AZdata= arrayListOf()

            Smooth_AXdata= arrayListOf()
            Smooth_AYdata= arrayListOf()
            Smooth_AZdata= arrayListOf()

            Agraph?.removeAllSeries()


            OL_AXdata_index = arrayListOf()
            OL_AYdata_index = arrayListOf()
            OL_AZdata_index = arrayListOf()
            pointsPlotted=0.0

            setUpSensorStuff()
        }
        btnEnd.setOnClickListener {
            sensorManager.unregisterListener(this)
            // pretreatment , 전처리

            // Set up Graph
            Aviewport = Agraph!!.viewport
            Aviewport.isXAxisBoundsManual=true
            Aviewport.isScrollable = true
            Aviewport.isScalable=true

            Vviewport = Vgraph!!.viewport
            Vviewport.isScrollable = true
            Vviewport.isXAxisBoundsManual = true
            Vviewport.isScalable=true

            Pviewport = Pgraph!!.viewport
            Pviewport.isScrollable = true
            Pviewport.isXAxisBoundsManual = true
            Pviewport.isScalable=true

            //Smooth
            // nl : number of past data
            // nr : number of futer data
            val sgFilter: SGFilter = SGFilter(31, 31)
            val coeffs : DoubleArray = SGFilter.computeSGCoefficients(31,31,10)

            var arr = AXdata!!.toTypedArray()
            var values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }

            var smoothlist : DoubleArray = sgFilter.smooth( values, coeffs)
            Log.e("TAG values.size : ",values.size.toString())

            for(j in smoothlist.indices){
                Smooth_AXdata.add(smoothlist[j])
                Good_AXdata.add(AXdata[j]-Smooth_AXdata[j])
                Log.e("TAG original : ",   Smooth_AXdata[j].toString())
                Log.e("TAG smooth item  : ",smoothlist[j].toString())
            }

            arr=AYdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }

            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_AYdata.add(smoothlist[j])
                Good_AYdata.add(AYdata[j]-Smooth_AYdata[j])
                Log.e("TAG original : ", Good_AYdata[j].toString())
                Log.e("TAG smooth item  : ",smoothlist[j].toString())
            }

            arr=AZdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }

            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_AZdata.add(smoothlist[j])
                Good_AZdata.add(AZdata[j]-Smooth_AZdata[j])
                Log.e("TAG original : ", Good_AZdata[j].toString())
                Log.e("TAG smooth item  : ",smoothlist[j].toString())
            }

            //--Smooth

            // A Visualization

            AXseries= LineGraphSeries()
            AYseries=LineGraphSeries()
            AZseries=LineGraphSeries()
            Good_AXseries = LineGraphSeries()
            Good_AYseries = LineGraphSeries()
            Good_AZseries = LineGraphSeries()
            Smooth_AXseries = LineGraphSeries()
            Smooth_AYseries = LineGraphSeries()
            Smooth_AZseries = LineGraphSeries()


            for(i in AXdata.indices) {
                Log.e("TAG index : ", i.toString())
                AXseries!!.appendData(DataPoint(i.toDouble() / 5.0, AXdata[i]), true, 100)
                AYseries!!.appendData(DataPoint(i.toDouble() / 5.0, AYdata[i]), true, 100)
                AZseries!!.appendData(DataPoint(i.toDouble() / 5.0, AZdata[i]), true, 100)

            }

            for(i in Good_AXdata.indices){
                Log.e("TAG index GOOD : ",i.toString())
                Good_AXseries!!.appendData(DataPoint(i.toDouble()/5.0,Good_AXdata[i]),true,100)
                Good_AYseries!!.appendData(DataPoint(i.toDouble()/5.0,Good_AYdata[i]),true,100)
                Good_AZseries!!.appendData(DataPoint(i.toDouble()/5.0,Good_AZdata[i]),true,100)
            }

            for(i in Smooth_AXdata.indices){
                Log.e("TAG index Smooth : ",i.toString())
                Smooth_AXseries!!.appendData(DataPoint(i.toDouble()/5.0,Smooth_AXdata[i]),true,100)
                Smooth_AYseries!!.appendData(DataPoint(i.toDouble()/5.0,Smooth_AYdata[i]),true,100)
                Smooth_AZseries!!.appendData(DataPoint(i.toDouble()/5.0,Smooth_AZdata[i]),true,100)
            }

            Agraph!!.addSeries(AXseries)
            Agraph!!.addSeries(Good_AXseries)
            Agraph!!.addSeries(Smooth_AXseries)
            AXseries!!.color = Color.BLUE
            Smooth_AXseries!!.color=Color.GREEN
            Good_AXseries!!.color = Color.RED

            // --A Visualization

            //Integrate
            for(i in 0..Good_AXdata.size-2){
                if(i==0){
                    VXdata+=((timedelta[i+1]-timedelta[i])*(Good_AXdata[i]+Good_AXdata[i+1])/2)
                    VYdata+=((timedelta[i+1]-timedelta[i])*(Good_AYdata[i]+Good_AYdata[i+1])/2)
                    VZdata+=((timedelta[i+1]-timedelta[i])*(Good_AZdata[i]+Good_AZdata[i+1])/2)
                }
                else {
                    VXdata += VXdata[i - 1] + ((timedelta[i + 1] - timedelta[i]) * (Good_AXdata[i] + Good_AXdata[i + 1]) / 2)
                    VYdata += VYdata[i-1]+((timedelta[i + 1] - timedelta[i]) * (Good_AYdata[i] + Good_AYdata[i + 1]) / 2)
                    VZdata += VZdata[i-1]+((timedelta[i + 1] - timedelta[i]) * (Good_AZdata[i] + Good_AZdata[i + 1]) / 2)
                }
            }
            //--Integrate

            //smooth V
            arr=VXdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_VXdata.add(smoothlist[j])
                Good_VXdata.add(VXdata[j]-Smooth_VXdata[j]);
            }

            arr=VYdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_VYdata.add(smoothlist[j])
                Good_VYdata.add(VYdata[j]-Smooth_VYdata[j]);
            }

            arr=VZdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_VZdata.add(smoothlist[j])
                Good_VZdata.add(VZdata[j]-Smooth_VZdata[j]);
            }
            //--smooth V

            // V visualization
            VXseries= LineGraphSeries()
            VYseries=LineGraphSeries()
            VZseries=LineGraphSeries()
            Good_VXseries = LineGraphSeries()
            Good_VYseries = LineGraphSeries()
            Good_VZseries = LineGraphSeries()
            Smooth_VXseries = LineGraphSeries()
            Smooth_VYseries = LineGraphSeries()
            Smooth_VZseries = LineGraphSeries()

            for(i in VXdata.indices) {
                Log.e("TAG index : ", i.toString()+ Good_AXdata[i].toString()+ VXdata[i].toString())
                VXseries!!.appendData(DataPoint(i.toDouble() / 5.0, VXdata[i]), true, 100)
                VYseries!!.appendData(DataPoint(i.toDouble() / 5.0, VYdata[i]), true, 100)
                VZseries!!.appendData(DataPoint(i.toDouble() / 5.0, VZdata[i]), true, 100)
                Smooth_VXseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_VXdata[i]), true, 100)
                Smooth_VYseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_VYdata[i]), true, 100)
                Smooth_VZseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_VZdata[i]), true, 100)
                Good_VXseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_VXdata[i]), true, 100)
                Good_VYseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_VYdata[i]), true, 100)
                Good_VZseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_VZdata[i]), true, 100)
            }

            Vgraph!!.addSeries(VXseries)
            Vgraph!!.addSeries(Smooth_VXseries)
            Vgraph!!.addSeries(Good_VXseries)
            VXseries!!.color=Color.BLUE
            Smooth_VXseries!!.color=Color.GREEN
            Good_VXseries!!.color=Color.RED

            //-- V visualization


            //Integrate
            for(i in 0..Good_VXdata.size-2){
                if(i==0){
                    PXdata+=((timedelta[i+1]-timedelta[i])*(Good_VXdata[i]+Good_VXdata[i+1])/2)
                    PYdata+=((timedelta[i+1]-timedelta[i])*(Good_VYdata[i]+Good_VYdata[i+1])/2)
                    PZdata+=((timedelta[i+1]-timedelta[i])*(Good_VZdata[i]+Good_VZdata[i+1])/2)
                }
                else {
                    PXdata += PXdata[i - 1] + ((timedelta[i + 1] - timedelta[i]) * (Good_VXdata[i] + Good_VXdata[i + 1]) / 2)
                    PYdata += PYdata[i-1]+((timedelta[i + 1] - timedelta[i]) * (Good_VYdata[i] + Good_VYdata[i + 1]) / 2)
                    PZdata += PZdata[i-1]+((timedelta[i + 1] - timedelta[i]) * (Good_VZdata[i] + Good_VZdata[i + 1]) / 2)
                }
            }
            //--Integrate

            //smooth P
            Log.e("PXdata",PXdata.size.toString())
            arr=PXdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_PXdata.add(smoothlist[j])
                Good_PXdata.add(PXdata[j]-Smooth_PXdata[j]);
            }

            arr=PYdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_PYdata.add(smoothlist[j])
                Good_PYdata.add(PYdata[j]-Smooth_PYdata[j]);
            }

            arr=PZdata!!.toTypedArray()
            values = DoubleArray(arr.size)
            for (j in arr.indices) {
                values[j] = arr[j]
            }
            smoothlist = sgFilter.smooth(values,coeffs)
            for(j in smoothlist.indices){
                Smooth_PZdata.add(smoothlist[j])
                Good_PZdata.add(PZdata[j]-Smooth_PZdata[j]);
            }
            //--smooth P

            // P visualization
            PXseries= LineGraphSeries()
            PYseries=LineGraphSeries()
            PZseries=LineGraphSeries()
            Good_PXseries = LineGraphSeries()
            Good_PYseries = LineGraphSeries()
            Good_PZseries = LineGraphSeries()
            Smooth_PXseries = LineGraphSeries()
            Smooth_PYseries = LineGraphSeries()
            Smooth_PZseries = LineGraphSeries()

            for(i in PXdata.indices) {
                Log.e("TAG index : ", i.toString()+ Good_PXdata[i].toString()+ PXdata[i].toString())
                PXseries!!.appendData(DataPoint(i.toDouble() / 5.0, PXdata[i]), true, 100)
                PYseries!!.appendData(DataPoint(i.toDouble() / 5.0, PYdata[i]), true, 100)
                PZseries!!.appendData(DataPoint(i.toDouble() / 5.0, PZdata[i]), true, 100)
                Smooth_PXseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_PXdata[i]), true, 100)
                Smooth_PYseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_PYdata[i]), true, 100)
                Smooth_PZseries!!.appendData(DataPoint(i.toDouble() / 5.0, Smooth_PZdata[i]), true, 100)
                Good_PXseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_PXdata[i]), true, 100)
                Good_PYseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_PYdata[i]), true, 100)
                Good_PZseries!!.appendData(DataPoint(i.toDouble() / 5.0, Good_PZdata[i]), true, 100)
            }

            Pgraph!!.addSeries(PXseries)
            Pgraph!!.addSeries(Smooth_PXseries)
            Pgraph!!.addSeries(Good_PXseries)
            PXseries!!.color=Color.BLUE
            Smooth_PXseries!!.color=Color.GREEN
            Good_PXseries!!.color=Color.RED

            //-- P visualization
        }
    }




    //이상값의 인덱스 반환
    private fun findOutLier( data : ArrayList<Double>) : ArrayList<Int>{
        Log.e("findOutLier : DataSize",data.size.toString())
        var ret : ArrayList<Int> = arrayListOf()
        var mList : ArrayList<Double> = arrayListOf()
        var scalar : ArrayList<Double> = arrayListOf()
        var _s : Int = data.size/2
        //스칼라 정렬 후 중간값 찾는 과정
        for(i in 0..data.size-1){
            scalar.add(abs(data[i]))
        }
        scalar.sort()
        var median = scalar[_s]
        //

        //중간값 편차 계산
        for (item in scalar){
            mList.add(abs(item-median))
        }
        mList.sort()
        var c = (-1/sqrt(2.0)*(-0.4769))
        //MAD = 중간값 편차의 3배 스케일링 * c
        var MAD = 4* mList[_s]*c
        //

        for((index,item) in data.withIndex()){
            if(median+MAD<item)
                ret.add(index)
            if(-median-MAD>item)
                ret.add(index)
        }
        Log.e("findOutLier List",data.toString())
        Log.e("findOutLier abs List",scalar.toString())
        Log.e("findOutLier Median",median.toString())
        Log.e("findOutLier MAD",MAD.toString())
        Log.e("findOutLier ret",ret.size.toString())
        return ret
    }

    //extraction good component
    private fun removeOutLier(data : ArrayList<Double>, target : ArrayList<Int>): ArrayList<Double>{
        var ret : ArrayList<Double> = arrayListOf()
        for((index,item) in data.withIndex()){
            if(target.contains(index))continue
            ret.add(item)
        }
        return ret
    }



    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager!!.registerListener(this,sensor,SensorManager.SENSOR_DELAY_GAME)
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
            var t : Long = System.currentTimeMillis()

            if(ison==false){
                ison=true
                startT= t
            }
            var diff : Long = System.currentTimeMillis()-startT
            timedelta.add(diff)
            AXdata.add(x)
            AYdata.add(y)
            AZdata.add(z)
            pointsPlotted++;


        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}

