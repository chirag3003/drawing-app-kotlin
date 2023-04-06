package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById<DrawingView>(R.id.drawingView)
        drawingView.setBrushSize(20f)

        val brushBtn = findViewById<ImageButton>(R.id.ic_brush)
        brushBtn.setOnClickListener{
            showBrushSizeDialog()
        }

    }

    private fun showBrushSizeDialog(){
        val brushD = Dialog(this)
        brushD.setContentView(R.layout.dialog_brush_size)
        brushD.setTitle("Brush Size: ")
//        brushD.show()

        val smBtn:ImageButton  = brushD.findViewById(R.id.ib_small_brush)
        val mdBtn  = brushD.findViewById<ImageButton>(R.id.ib_medium_brush)
        val lgBtn  = brushD.findViewById<ImageButton>(R.id.ib_large_brush)
        smBtn.setOnClickListener() {
            drawingView.setBrushSize(10f)
            brushD.dismiss()
        }
        mdBtn.setOnClickListener{
            drawingView.setBrushSize(20f)
            brushD.dismiss()
        }
        lgBtn.setOnClickListener{
            drawingView.setBrushSize(30f)
            brushD.dismiss()
        }
        brushD.show()

    }

}