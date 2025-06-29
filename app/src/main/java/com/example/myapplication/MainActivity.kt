package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {

    lateinit var result:TextView
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val camera=findViewById<ImageView>(R.id.imageView2)
        val copy=findViewById<ImageView>(R.id.imageView3)
        result=findViewById(R.id.resultTV)
        supportActionBar?.hide()
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val extras = data?.extras
                val bitmap = extras?.get("data") as? Bitmap
                bitmap?.let {
                    detect(it)
                }
            }
        }
        camera.setOnClickListener {
            startCamera()
        }

        copy.setOnClickListener{
            val clip=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val cl=ClipData.newPlainText("label",result.text.toString())
            clip.setPrimaryClip(cl)
            Toast.makeText(this,"Copied",Toast.LENGTH_SHORT).show()
        }
    }
    fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun detect(bitmap: Bitmap) {
        // When using Latin script library
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)
        val r = recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfull
                result.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                Toast.makeText(this,"Sahi karvao camera",Toast.LENGTH_SHORT).show()
            }

    }
}