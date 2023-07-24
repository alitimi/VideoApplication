package com.android.pupildetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity2 : AppCompatActivity() {

    companion object{
        init{
            System.loadLibrary("opencv_java4")
            System.loadLibrary("native-lib")
        }
    }

    external fun ConvertRGBtoGray(a: Long, b: Long)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView(){
        jcv_camera.visibility = SurfaceView.VISIBLE
        jcv_camera.setCameraIndex(1) // front 1, back 0
    }
}