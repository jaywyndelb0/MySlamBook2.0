package com.kodego.diangca.ebrahim.myslambook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class TestPictureActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create a test SlamBook object
        val testSlamBook = SlamBook().apply {
            firstName = "Test"
            lastName = "User"
            nickName = "Tester"
            birthDate = "01/01/1999"
            address = "Test Address"
            contactNo = "123456789"
            email = "test@example.com"
            gender = "Male"
            status = "Single"
            defineLove = "Love is beautiful"
            defineFriendship = "Friendship is precious"
            memorableExperience = "Testing this app"
            describeMe = "I am a tester"
            adviceForMe = "Keep testing"
            rateMe = 5
        }
        
        // Navigate directly to Form3Activity
        val intent = Intent(this, Form3Activity::class.java)
        intent.putExtra("slamBook", testSlamBook)
        startActivity(intent)
        finish()
    }
}