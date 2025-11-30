package com.kodego.diangca.ebrahim.myslambook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PictureTestActivity : AppCompatActivity() {
    
    private lateinit var imageView: ImageView
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button
    private var currentPhotoPath: String? = null
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }
    
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            Toast.makeText(this, "Photo captured successfully!", Toast.LENGTH_SHORT).show()
            currentPhotoPath?.let { path ->
                loadImageIntoImageView(Uri.fromFile(File(path)))
            }
        } else {
            Toast.makeText(this, "Photo capture cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }
    
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            Toast.makeText(this, "Image selected from gallery!", Toast.LENGTH_SHORT).show()
            loadImageIntoImageView(uri)
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a simple layout programmatically
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 50, 50, 50)
        
        // Create ImageView
        imageView = ImageView(this)
        imageView.layoutParams = android.widget.LinearLayout.LayoutParams(300, 300)
        imageView.setImageResource(R.drawable.profile_icon)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        layout.addView(imageView)
        
        // Create Camera Button
        cameraButton = Button(this)
        cameraButton.text = "Take Photo"
        cameraButton.setOnClickListener { takePicture() }
        layout.addView(cameraButton)
        
        // Create Gallery Button
        galleryButton = Button(this)
        galleryButton.text = "Browse Gallery"
        galleryButton.setOnClickListener { browseImage() }
        layout.addView(galleryButton)
        
        setContentView(layout)
        
        Toast.makeText(this, "Picture Test Activity Loaded", Toast.LENGTH_LONG).show()
    }
    
    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            return
        }
        
        try {
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(this, "com.kodego.diangca.ebrahim.myslambook.fileprovider", photoFile)
            takePictureLauncher.launch(photoURI)
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(this, "Error launching camera: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun browseImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            return
        }
        
        pickImageLauncher.launch("image/*")
    }
    
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    private fun loadImageIntoImageView(uri: Uri) {
        try {
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(imageView)
            Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    browseImage()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}