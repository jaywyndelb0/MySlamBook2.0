package com.kodego.diangca.ebrahim.myslambook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class FormActivity : AppCompatActivity() {

    private lateinit var slamBook: SlamBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the SlamBook object from intent
        slamBook = intent.getParcelableExtra("slamBooK") ?: SlamBook()
        
        // Immediately redirect to Form1Activity to start the proper form sequence
        val intent = Intent(this, Form1Activity::class.java)
        intent.putExtra("slamBooK", slamBook)
        
        // Pass edit mode extras if they exist
        if (getIntent().hasExtra("edit_mode")) {
            intent.putExtra("edit_mode", getIntent().getBooleanExtra("edit_mode", false))
            intent.putExtra("edit_position", getIntent().getIntExtra("edit_position", -1))
        }
        
        startActivity(intent)
        finish()
    }
}