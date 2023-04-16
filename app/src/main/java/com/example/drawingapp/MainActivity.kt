package com.example.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var imageButtonCurrentPaint: ImageButton
    private var customProgressDialog:Dialog?=null
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val bgImage: ImageView = findViewById(R.id.iv_background)
                bgImage.setImageURI(result.data?.data)
            }
        }
    private val activityLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.map {
                val pName = it.key
                val pIsGranted = it.value
                if (pIsGranted) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(intent)
                } else
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById<DrawingView>(R.id.drawing_view)
        drawingView.setBrushSize(20f)

        val linearLayoutColors = findViewById<LinearLayout>(R.id.ll_paint_colors)

        imageButtonCurrentPaint = linearLayoutColors[1] as ImageButton
        imageButtonCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_pressed
            )
        )

        val brushBtn = findViewById<ImageButton>(R.id.ib_brush)
        brushBtn.setOnClickListener {
            showBrushSizeDialog()
        }

        val galleryBtn = findViewById<ImageButton>(R.id.ib_gallery)
        galleryBtn.setOnClickListener() {
            requestPermission()
        }

        val undoBtn = findViewById<ImageButton>(R.id.ib_undo)
        undoBtn.setOnClickListener() {
            drawingView.undoDrawing()
        }
        val saveBtn = findViewById<ImageButton>(R.id.ib_save)
        saveBtn.setOnClickListener() {
            lifecycleScope.launch {
                val bitmap = getBitmapFromView(findViewById(R.id.fl_drawing_view_container))
                saveFile(bitmap)
            }

        }

    }

    private fun showBrushSizeDialog() {
        val brushD = Dialog(this)
        brushD.setContentView(R.layout.dialog_brush_size)
        brushD.setTitle("Brush Size: ")
        val smBtn: ImageButton = brushD.findViewById(R.id.ib_small_brush)
        val mdBtn = brushD.findViewById<ImageButton>(R.id.ib_medium_brush)
        val lgBtn = brushD.findViewById<ImageButton>(R.id.ib_large_brush)
        smBtn.setOnClickListener() {
            drawingView.setBrushSize(10f)
            brushD.dismiss()
        }
        mdBtn.setOnClickListener {
            drawingView.setBrushSize(20f)
            brushD.dismiss()
        }
        lgBtn.setOnClickListener {
            drawingView.setBrushSize(30f)
            brushD.dismiss()
        }
        brushD.show()

    }

    private fun requestPermission() {
        println("it was called")
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Permission Required");
            builder.setMessage("We need media permissions for this feature")
            builder.setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        } else {
            activityLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun paintClicked(view: View) {
        if (view != imageButtonCurrentPaint) {
            imageButtonCurrentPaint.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_normal
                )
            )
            imageButtonCurrentPaint = view as ImageButton
            val cTag = imageButtonCurrentPaint.tag.toString()
            drawingView.setColor(cTag)
            imageButtonCurrentPaint.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_pressed
                )
            )
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitm = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitm)
        val background = view.background
        if (view.background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitm
    }


    private suspend fun saveFile(bitmap: Bitmap?): String {
        var result = ""
        showProgressDialog()
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(
                        externalCacheDir?.absoluteFile.toString()
                                + File.separator + "KidsDrawingApp" + System.currentTimeMillis() / 1000 + ".png"
                    )
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = f.absolutePath
                    println(result)
                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "Saved on $result", Toast.LENGTH_LONG)
                                .show()
                        }
                        customProgressDialog?.dismiss()
                    }
                } catch (err: Error) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Something Went Wrong", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
        return result
    }

    private fun showProgressDialog(){
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.progress_dialog)
        customProgressDialog?.show()
    }

}