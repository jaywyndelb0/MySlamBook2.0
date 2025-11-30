package com.kodego.diangca.ebrahim.myslambook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kodego.diangca.ebrahim.myslambook.databinding.ActivitySlamBookDetailsBinding
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class SlamBookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlamBookDetailsBinding
    private lateinit var slamBook: SlamBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlamBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the SlamBook object from intent
        slamBook = intent.getParcelableExtra("slam_book") ?: SlamBook()

        setupViews()
        populateData()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupViews() {
        // Any additional view setup can go here
    }

    private fun populateData() {
        // Personal Information
        val fullName = "${slamBook.firstName ?: "Unknown"} ${slamBook.lastName ?: ""}".trim()
        binding.textFullName.text = "Full Name: $fullName"
        
        binding.textNickname.text = "Nickname: ${slamBook.nickName ?: "Not provided"}"
        binding.textEmail.text = "Email: ${slamBook.email ?: "Not provided"}"
        binding.textBirthDate.text = "Birth Date: ${slamBook.birthDate ?: "Not provided"}"
        binding.textGender.text = "Gender: ${slamBook.gender ?: "Not provided"}"
        binding.textContactNo.text = "Contact: ${slamBook.contactNo ?: "Not provided"}"
        binding.textAddress.text = "Address: ${slamBook.address ?: "Not provided"}"

        // Personal Thoughts
        binding.textDefineLove.text = "Define Love: ${slamBook.defineLove ?: "Not provided"}"
        binding.textDefineFriendship.text = "Define Friendship: ${slamBook.defineFriendship ?: "Not provided"}"
        binding.textMemorableExperience.text = "Memorable Experience: ${slamBook.memorableExperience ?: "Not provided"}"
        binding.textDescribeMe.text = "Describe Me: ${slamBook.describeMe ?: "Not provided"}"
        binding.textAdviceForMe.text = "Advice for Me: ${slamBook.adviceForMe ?: "Not provided"}"
    }
}