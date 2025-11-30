package com.kodego.diangca.ebrahim.myslambook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.kodego.diangca.ebrahim.myslambook.databinding.ActivityForm2Binding
import com.kodego.diangca.ebrahim.myslambook.adapter.AdapterHobbies
import com.kodego.diangca.ebrahim.myslambook.adapter.AdapterMovie
import com.kodego.diangca.ebrahim.myslambook.adapter.AdapterSkill
import com.kodego.diangca.ebrahim.myslambook.adapter.AdapterSong
import com.kodego.diangca.ebrahim.myslambook.model.*
import com.kodego.diangca.ebrahim.myslambook.utils.ValidationUtils

class Form2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm2Binding
    private lateinit var slamBook: SlamBook


    private lateinit var adapterSong: AdapterSong
    private var songs: ArrayList<Song> = ArrayList()

    private lateinit var adapterMovie: AdapterMovie
    private var movies: ArrayList<Movie> = ArrayList()

    private lateinit var adapterHobbies: AdapterHobbies
    private var hobbies: ArrayList<Hobbies> = ArrayList()

    private lateinit var adapterSkill: AdapterSkill
    private var skills: ArrayList<Skill> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        adapterSong = AdapterSong(this, songs)
        binding.favSongList.layoutManager = LinearLayoutManager(applicationContext)
        binding.favSongList.adapter = adapterSong

        adapterMovie = AdapterMovie(this, movies)
        binding.favMovieList.layoutManager = LinearLayoutManager(applicationContext)
        binding.favMovieList.adapter = adapterMovie

        adapterHobbies = AdapterHobbies(this, hobbies)
        binding.hobbiesList.layoutManager = LinearLayoutManager(applicationContext)
        binding.hobbiesList.adapter = adapterHobbies

        adapterSkill = AdapterSkill(this, skills)
        binding.skillList.layoutManager = LinearLayoutManager(applicationContext)
        binding.skillList.adapter = adapterSkill

        slamBook = intent.getParcelableExtra("slamBooK") ?: SlamBook()

        Snackbar.make(
            binding.root,
            "Hi ${slamBook.lastName}, ${slamBook.lastName}!, Please complete the following fields. Thank you!",
            Snackbar.LENGTH_SHORT
        ).show()

        binding.btnAddFavSong.setOnClickListener {
            btnAddOnClickListener(binding.root, "Song", binding.songName, binding.favSongList)
        }
        binding.btnAddFavMov.setOnClickListener {
            btnAddOnClickListener(binding.root, "Movie", binding.movieName, binding.favMovieList)
        }

        binding.btnAddHobbies.setOnClickListener {
            btnAddOnClickListener(binding.root, "Hobbies", binding.hobbies, binding.hobbiesList)
        }

        binding.btnAddSkill.setOnClickListener {
            btnAddOnClickListener(binding.root, "Skills", binding.skill, binding.skillList)
        }


        binding.btnBack.setOnClickListener {
            btnBackOnClickListener()
        }
        binding.btnNext.setOnClickListener {
            btnNextOnClickListener()
        }
    }

    private fun btnAddOnClickListener(
        view: View?,
        type: String,
        field: TextInputEditText,
        recyclerView: RecyclerView
    ) {
        val text = field.text.toString()

        // Validate input based on type
        val validation = when (type) {
            "Song" -> {
                ValidationUtils.validateTextField(
                    text, 
                    "Song name", 
                    minLength = 1, 
                    maxLength = 100
                )
            }
            "Movie" -> {
                ValidationUtils.validateTextField(
                    text, 
                    "Movie name", 
                    minLength = 1, 
                    maxLength = 100
                )
            }
            "Hobbies" -> {
                ValidationUtils.validateTextField(
                    text, 
                    "Hobby", 
                    minLength = 2, 
                    maxLength = 50
                )
            }
            "Skills" -> {
                ValidationUtils.validateTextField(
                    text, 
                    "Skill name", 
                    minLength = 2, 
                    maxLength = 50
                )
            }
            else -> ValidationUtils.ValidationResult(false, "Invalid type")
        }

        // Apply validation to field
        ValidationUtils.applyValidationToTextInputEditText(field, validation)
        
        if (!validation.isValid) {
            Snackbar.make(binding.root, validation.errorMessage ?: "Please check the input.", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
            return
        }

        // Additional validation for skills (rate selection)
        if (type == "Skills") {
            val rateValidation = ValidationUtils.validateSpinner(binding.skillRate, "skill rate")
            if (!rateValidation.isValid) {
                Snackbar.make(binding.root, rateValidation.errorMessage ?: "Please select skill rate first.", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.error_red))
                    .setTextColor(getColor(R.color.white))
                    .show()
                return
            }
        }

        // Check for duplicates
        val isDuplicate = when (type) {
            "Song" -> songs.any { it.songName.equals(text, ignoreCase = true) }
            "Movie" -> movies.any { it.movieName.equals(text, ignoreCase = true) }
            "Hobbies" -> hobbies.any { it.hobbie.equals(text, ignoreCase = true) }
            "Skills" -> skills.any { it.skill.equals(text, ignoreCase = true) }
            else -> false
        }

        if (isDuplicate) {
            Snackbar.make(binding.root, "This $type already exists in your list.", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
            return
        }

        // Add to appropriate list
        when (type) {
            "Song" -> {
                songs.add(Song(text))
            }
            "Movie" -> {
                movies.add(Movie(text))
            }
            "Hobbies" -> {
                hobbies.add(Hobbies(text))
            }
            "Skills" -> {
                skills.add(Skill(text, binding.skillRate.selectedItemPosition))
                binding.skillRate.setSelection(0) // Reset rate selection
            }
        }
        
        Snackbar.make(binding.root, "$type has been successfully added.", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(R.color.primary))
            .setTextColor(getColor(R.color.white))
            .show()

        field.setText("")
        field.error = null // Clear any previous errors
        recyclerView.adapter!!.notifyDataSetChanged()

        // Hide keyboard and focus on recycler view
        if (view != null) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            recyclerView.requestFocus()
        }
    }


    private fun btnNextOnClickListener() {
        // Validate that user has added minimum required items
        val validator = ValidationUtils.FormValidator()
        
        // Validate favorite songs (at least 1)
        val songsValidation = ValidationUtils.validateList(
            songs, 
            "favorite song", 
            minItems = 1
        )
        validator.validate(songsValidation, "Favorite Songs")
        
        // Validate favorite movies (at least 1)
        val moviesValidation = ValidationUtils.validateList(
            movies, 
            "favorite movie", 
            minItems = 1
        )
        validator.validate(moviesValidation, "Favorite Movies")
        
        // Validate hobbies (at least 1)
        val hobbiesValidation = ValidationUtils.validateList(
            hobbies, 
            "hobby", 
            minItems = 1
        )
        validator.validate(hobbiesValidation, "Hobbies")
        
        // Validate skills (at least 1)
        val skillsValidation = ValidationUtils.validateList(
            skills, 
            "skill", 
            minItems = 1
        )
        validator.validate(skillsValidation, "Skills")
        
        // Show validation errors if any
        if (!validator.isValid()) {
            val errorMessage = if (validator.getErrors().size == 1) {
                validator.getFirstError() ?: "Please complete all required fields"
            } else {
                "Please add at least one item for each category:\n${validator.getAllErrorsAsString()}"
            }
            
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
            return
        }
        
        // Save the lists data to slam book
        slamBook.favoriteSongs = songs
        slamBook.favoriteMovies = movies
        slamBook.hobbies = hobbies
        slamBook.skillsWithRate = skills
        
        // Show success message
        Snackbar.make(binding.root, "✅ All preferences saved successfully!", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(R.color.primary))
            .setTextColor(getColor(R.color.white))
            .show()
        
        val nextForm = Intent(this, Form3Activity::class.java)
        nextForm.putExtra("slamBooK", slamBook)
        
        // Pass edit mode extras if they exist
        if (intent.hasExtra("edit_mode")) {
            nextForm.putExtra("edit_mode", intent.getBooleanExtra("edit_mode", false))
            nextForm.putExtra("edit_position", intent.getIntExtra("edit_position", -1))
        }
        
        startActivity(nextForm)
        finish()
    }

    private fun btnBackOnClickListener() {
        val intent = Intent(this, Form1Activity::class.java)
        intent.putExtra("slamBooK", slamBook)
        startActivity(intent)
        finish()
    }
}