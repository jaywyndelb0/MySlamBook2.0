package com.kodego.diangca.ebrahim.myslambook

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kodego.diangca.ebrahim.myslambook.data.SlamBookDataManager
import com.kodego.diangca.ebrahim.myslambook.databinding.ActivityForm3Binding
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook
import com.kodego.diangca.ebrahim.myslambook.utils.ValidationUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Form3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm3Binding
    private lateinit var slamBook: SlamBook
    private lateinit var dataManager: SlamBookDataManager
    
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var isEditMode: Boolean = false
    private var editPosition: Int = -1
    
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }
    
    // Activity result launcher for camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri?.let { uri ->
                Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
                loadImageIntoImageView(uri)
                slamBook.profilePicUri = uri.toString()
            }
        } else {
            Toast.makeText(this, "Photo capture cancelled or failed", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Activity result launcher for gallery
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
            loadImageIntoImageView(it)
            slamBook.profilePicUri = it.toString()
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = SlamBookDataManager(this)
        
        slamBook = intent.getParcelableExtra("slamBooK") ?: SlamBook()
        
        // Check if we're in edit mode
        isEditMode = intent.getBooleanExtra("edit_mode", false)
        editPosition = intent.getIntExtra("edit_position", -1)

        binding.btnSave.setOnClickListener {
            btnSaveOnClickListener()
        }
        
        binding.btnCamera.setOnClickListener {
            takePicture()
        }
        
        binding.btnBrowse.setOnClickListener {
            browseImage()
        }
        
        // Load existing profile picture if available
        slamBook.profilePicUri?.let { uri ->
            loadImageIntoImageView(Uri.parse(uri))
        }
    }

    private fun btnSaveOnClickListener() {
        // Clear previous errors
        binding.defineLove.error = null
        binding.defineFriendship.error = null
        binding.memorableExperience.error = null
        binding.describeMe.error = null
        binding.adviceForMe.error = null
        
        // Create form validator
        val validator = ValidationUtils.FormValidator()
        
        // Validate "Define Love" field
        val defineLoveText = binding.defineLove.text.toString()
        val defineLoveValidation = ValidationUtils.validateTextField(
            defineLoveText, 
            "definition of love", 
            minLength = 10, 
            maxLength = 500
        )
        validator.validate(defineLoveValidation, "Define Love")
        if (!defineLoveValidation.isValid) {
            binding.defineLove.error = defineLoveValidation.errorMessage
        }
        
        // Validate "Define Friendship" field
        val defineFriendshipText = binding.defineFriendship.text.toString()
        val defineFriendshipValidation = ValidationUtils.validateTextField(
            defineFriendshipText, 
            "definition of friendship", 
            minLength = 10, 
            maxLength = 500
        )
        validator.validate(defineFriendshipValidation, "Define Friendship")
        if (!defineFriendshipValidation.isValid) {
            binding.defineFriendship.error = defineFriendshipValidation.errorMessage
        }
        
        // Validate "Memorable Experience" field
        val memorableExperienceText = binding.memorableExperience.text.toString()
        val memorableExperienceValidation = ValidationUtils.validateTextField(
            memorableExperienceText, 
            "memorable experience", 
            minLength = 20, 
            maxLength = 1000
        )
        validator.validate(memorableExperienceValidation, "Memorable Experience")
        if (!memorableExperienceValidation.isValid) {
            binding.memorableExperience.error = memorableExperienceValidation.errorMessage
        }
        
        // Validate "Describe Me" field
        val describeMeText = binding.describeMe.text.toString()
        val describeMeValidation = ValidationUtils.validateTextField(
            describeMeText, 
            "description about yourself", 
            minLength = 10, 
            maxLength = 500
        )
        validator.validate(describeMeValidation, "Describe Me")
        if (!describeMeValidation.isValid) {
            binding.describeMe.error = describeMeValidation.errorMessage
        }
        
        // Validate "Advice For Me" field
        val adviceForMeText = binding.adviceForMe.text.toString()
        val adviceForMeValidation = ValidationUtils.validateTextField(
            adviceForMeText, 
            "advice", 
            minLength = 10, 
            maxLength = 500
        )
        validator.validate(adviceForMeValidation, "Advice For Me")
        if (!adviceForMeValidation.isValid) {
            binding.adviceForMe.error = adviceForMeValidation.errorMessage
        }
        
        // Validate rating (must be at least 1)
        val rating = binding.ratingBar.rating
        val ratingValidation = ValidationUtils.validateRating(rating, "Rating")
        validator.validate(ratingValidation, "Rating")
        
        // Validate profile picture (optional but recommended)
        val hasProfilePicture = selectedImageUri != null || !slamBook.profilePicUri.isNullOrEmpty()
        
        // Show validation errors if any
        if (!validator.isValid()) {
            val errorMessage = if (validator.getErrors().size == 1) {
                validator.getFirstError() ?: "Please complete all required fields"
            } else {
                "Please fix the following errors:\n${validator.getAllErrorsAsString()}"
            }
            
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
            return
        }
        
        // Show warning if no profile picture
        if (!hasProfilePicture) {
            Snackbar.make(binding.root, "⚠️ Consider adding a profile picture to complete your slam book!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.warning_orange))
                .setTextColor(getColor(R.color.white))
                .show()
        }
        
        // Save the final form data
        slamBook.defineLove = defineLoveText
        slamBook.defineFriendship = defineFriendshipText
        slamBook.memorableExperience = memorableExperienceText
        slamBook.describeMe = describeMeText
        slamBook.adviceForMe = adviceForMeText
        slamBook.rateMe = rating.toInt()

        // Save to local storage
        if (isEditMode && editPosition != -1) {
            dataManager.updateSlamBook(editPosition, slamBook)
            Snackbar.make(binding.root, "🎉 Slam book updated successfully!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.primary))
                .setTextColor(getColor(R.color.white))
                .show()
        } else {
            dataManager.saveSlamBook(slamBook)
            Snackbar.make(binding.root, "🎉 Slam book saved successfully!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.primary))
                .setTextColor(getColor(R.color.white))
                .show()
        }
        
        // Navigate back to menu
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
    
    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            launchCamera()
        }
    }
    
    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            selectedImageUri = FileProvider.getUriForFile(
                this,
                "com.kodego.diangca.ebrahim.myslambook.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(selectedImageUri)
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(this, "Error launching camera: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun browseImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
        } else {
            pickImageLauncher.launch("image/*")
        }
    }
    
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(null)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    private fun loadImageIntoImageView(uri: Uri) {
        try {
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(binding.profileImage)
            Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageLauncher.launch("image/*")
                } else {
                    Toast.makeText(this, "Storage permission is required to browse images", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}