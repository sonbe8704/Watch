package com.practice.watch

import android.content.Intent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tv_tilting : TextView = findViewById(R.id.tv_tilting)
        var tv_graph : TextView= findViewById(R.id.tv_graph)
        var tv_filering : TextView = findViewById(R.id.tv_filtering)
        var tv_savtzky : TextView = findViewById(R.id.tv_savtzky)
        tv_filering.setOnClickListener {
            Log.e("TAG", "CLick")
            val intent = Intent(this,FilteringActivity::class.java)
            startActivity(intent)
        }
        tv_graph.setOnClickListener {
            val intent = Intent(this,GraphActivity::class.java)
            startActivity(intent)
        }
        tv_tilting.setOnClickListener {
            val intent = Intent(this,TiltingActivity::class.java)
            startActivity(intent)
        }
        tv_savtzky.setOnClickListener {
            val intent = Intent(this,MotionDriftActivity::class.java)
            startActivity(intent)
        }
    }


}