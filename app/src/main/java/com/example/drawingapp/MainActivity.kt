package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var imageButtonCurrentPaint:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById<DrawingView>(R.id.drawing_view)
        drawingView.setBrushSize(20f)

        val linearLayoutColors = findViewById<LinearLayout>(R.id.ll_paint_colors)

        imageButtonCurrentPaint = linearLayoutColors[1] as ImageButton
        imageButtonCurrentPaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

        val brushBtn = findViewById<ImageButton>(R.id.ib_brush)
        brushBtn.setOnClickListener{
            showBrushSizeDialog()
        }

    }

    private fun showBrushSizeDialog(){
        val brushD = Dialog(this)
        brushD.setContentView(R.layout.dialog_brush_size)
        brushD.setTitle("Brush Size: ")
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


    fun paintClicked(view: View){
        if(view != imageButtonCurrentPaint){
            imageButtonCurrentPaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_normal))
            imageButtonCurrentPaint = view as ImageButton
            val cTag = imageButtonCurrentPaint.tag.toString()
            drawingView.setColor(cTag)
            imageButtonCurrentPaint.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))
        }
    }

}