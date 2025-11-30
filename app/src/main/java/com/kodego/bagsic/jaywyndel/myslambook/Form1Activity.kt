package com.kodego.diangca.ebrahim.myslambook

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.kodego.diangca.ebrahim.myslambook.databinding.ActivityForm1Binding
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook
import com.kodego.diangca.ebrahim.myslambook.utils.ValidationUtils
import java.util.*

class Form1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm1Binding
    private lateinit var slamBook: SlamBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent!=null) {
            slamBook = intent.getParcelableExtra("slamBooK") ?: SlamBook()
        } else {
            slamBook = SlamBook()
        }
        binding.btnBack.setOnClickListener {
            btnBackOnClickListener()
        }
        binding.btnNext.setOnClickListener {
            btnNextOnClickListener()
        }
        
        // Add calendar functionality for date selection
        setupDatePicker()
        
        // Add input validation listeners
        setupInputValidation()
        
        // Populate form fields if data exists (when navigating back from Form2)
        populateFormFields()

    }

    private fun btnNextOnClickListener() {
        // Validate all fields with specific error messages
        if (!validateForm()) {
            return
        }


        slamBook.nickName = binding.nickName.text.toString()
        slamBook.friendCallMe = binding.friendCall.text.toString()
        slamBook.likeToCallMe = binding.likeToCall.text.toString()
        slamBook.lastName = binding.lastName.text.toString()
        slamBook.firstName = binding.firstName.text.toString()
        slamBook.birthDate = if (binding.dateDisplay.text.toString() != "Tap here to select your birth date") {
            binding.dateDisplay.text.toString()
        } else {
            null
        }
        slamBook.gender = binding.gender.selectedItem.toString()
        slamBook.status = binding.status.selectedItem.toString()
        slamBook.email = binding.emailAdd.text.toString()
        slamBook.contactNo = binding.contactNo.text.toString()
        slamBook.address = binding.address.text.toString()

        Log.d(
            "FORM 1",
            "Hi ${slamBook.lastName}, ${slamBook.lastName}!, Please complete the following fields. Thank you!"
        )

        val nextForm = Intent(this, Form2Activity::class.java)
        nextForm.putExtra("slamBooK", slamBook)
        
        // Pass edit mode extras if they exist
        if (intent.hasExtra("edit_mode")) {
            nextForm.putExtra("edit_mode", intent.getBooleanExtra("edit_mode", false))
            nextForm.putExtra("edit_position", intent.getIntExtra("edit_position", -1))
        }
        
        startActivity(nextForm)
        finish()
    }
    
    private fun validateForm(): Boolean {
        // Clear previous errors
        binding.firstName.error = null
        binding.lastName.error = null
        binding.nickName.error = null
        binding.friendCall.error = null
        binding.likeToCall.error = null
        binding.emailAdd.error = null
        binding.contactNo.error = null
        binding.address.error = null
        
        // Use FormValidator for comprehensive validation
        val validator = ValidationUtils.FormValidator()
        
        // Validate first name
        val firstNameValidation = ValidationUtils.validateName(
            binding.firstName.text?.toString(), 
            "First name"
        )
        ValidationUtils.applyValidationToEditText(binding.firstName, firstNameValidation)
        validator.validate(firstNameValidation)
        
        // Validate last name
        val lastNameValidation = ValidationUtils.validateName(
            binding.lastName.text?.toString(), 
            "Last name"
        )
        ValidationUtils.applyValidationToEditText(binding.lastName, lastNameValidation)
        validator.validate(lastNameValidation)
        
        // Validate nickname
        val nicknameValidation = ValidationUtils.validateNickname(
            binding.nickName.text?.toString()
        )
        ValidationUtils.applyValidationToEditText(binding.nickName, nicknameValidation)
        validator.validate(nicknameValidation)
        
        // Validate friend call field
        val friendCallValidation = ValidationUtils.validateTextField(
            binding.friendCall.text?.toString(),
            "What friends call you",
            minLength = 2,
            maxLength = 30
        )
        ValidationUtils.applyValidationToEditText(binding.friendCall, friendCallValidation)
        validator.validate(friendCallValidation)
        
        // Validate like to call field
        val likeToCallValidation = ValidationUtils.validateTextField(
            binding.likeToCall.text?.toString(),
            "What you like to be called",
            minLength = 2,
            maxLength = 30
        )
        ValidationUtils.applyValidationToEditText(binding.likeToCall, likeToCallValidation)
        validator.validate(likeToCallValidation)
        
        // Validate email
        val emailValidation = ValidationUtils.validateEmail(
            binding.emailAdd.text?.toString()
        )
        ValidationUtils.applyValidationToEditText(binding.emailAdd, emailValidation)
        validator.validate(emailValidation)
        
        // Validate contact number
        val phoneValidation = ValidationUtils.validatePhoneNumber(
            binding.contactNo.text?.toString()
        )
        ValidationUtils.applyValidationToEditText(binding.contactNo, phoneValidation)
        validator.validate(phoneValidation)
        
        // Validate address
        val addressValidation = ValidationUtils.validateAddress(
            binding.address.text?.toString()
        )
        ValidationUtils.applyValidationToEditText(binding.address, addressValidation)
        validator.validate(addressValidation)
        
        // Validate birth date
        val dateText = binding.dateDisplay.text.toString()
        val dateValidation = ValidationUtils.validateDate(
            dateText, 
            "Tap here to select your birth date"
        )
        validator.validate(dateValidation)
        if (!dateValidation.isValid) {
            Snackbar.make(binding.root, dateValidation.errorMessage ?: "Please select your birth date", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
        }
        
        // Validate gender
        val genderValidation = ValidationUtils.validateSpinner(binding.gender, "gender")
        validator.validate(genderValidation)
        if (!genderValidation.isValid) {
            Toast.makeText(this, genderValidation.errorMessage, Toast.LENGTH_SHORT).show()
        }
        
        // Validate status
        val statusValidation = ValidationUtils.validateSpinner(binding.status, "status")
        validator.validate(statusValidation)
        if (!statusValidation.isValid) {
            Toast.makeText(this, statusValidation.errorMessage, Toast.LENGTH_SHORT).show()
        }
        
        // Show comprehensive error message if validation fails
        if (!validator.isValid()) {
            val errorMessage = if (validator.getErrors().size == 1) {
                validator.getFirstError() ?: "Please fix the error above"
            } else {
                "Please fix the ${validator.getErrors().size} errors above"
            }
            
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error_red))
                .setTextColor(getColor(R.color.white))
                .show()
        }
        
        return validator.isValid()
    }
    
    private fun setupDatePicker() {
        // Create a click listener for date selection
        val dateClickListener = View.OnClickListener {
            showCustomDatePicker()
        }
        
        // Add click listener to the date picker container
        binding.datePickerContainer.setOnClickListener(dateClickListener)
        
        // Add visual feedback when hovering/touching the date picker
        binding.datePickerContainer.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    view.alpha = 0.7f
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    view.alpha = 1.0f
                }
            }
            false // Let the click listener handle the actual click
        }
    }
    
    private fun showCustomDatePicker() {
        // Set default date to 18 years ago for better user experience
        val defaultCalendar = Calendar.getInstance()
        defaultCalendar.add(Calendar.YEAR, -18) // Default to 18 years old
        
        // If there's already a selected date, use that as the default
        val currentDateText = binding.dateDisplay.text.toString()
        var defaultYear = defaultCalendar.get(Calendar.YEAR)
        var defaultMonth = defaultCalendar.get(Calendar.MONTH)
        var defaultDay = defaultCalendar.get(Calendar.DAY_OF_MONTH)
        
        // Try to parse existing date if it's already selected
        if (currentDateText != "Tap here to select your birth date" && currentDateText.contains(",")) {
            try {
                val monthNames = arrayOf(
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
                )
                
                val parts = currentDateText.split(" ")
                if (parts.size >= 3) {
                    val monthName = parts[0]
                    val day = parts[1].replace(",", "").toInt()
                    val year = parts[2].toInt()
                    val monthIndex = monthNames.indexOf(monthName)
                    
                    if (monthIndex != -1) {
                        defaultYear = year
                        defaultMonth = monthIndex
                        defaultDay = day
                    }
                }
            } catch (e: Exception) {
                // If parsing fails, use the default 18 years ago
            }
        }
        
        // Create a simple date picker dialog
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.CustomDatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Directly apply the selected date without confirmation
                applySelectedDate(selectedYear, selectedMonth, selectedDay)
            },
            defaultYear, defaultMonth, defaultDay
        )
        
        // Configure the date picker for easier year selection
        val datePicker = datePickerDialog.datePicker
        
        // Set max date to today (can't be born in the future)
        datePicker.maxDate = System.currentTimeMillis()
        
        // Set min date to 120 years ago (more reasonable range)
        val minCalendar = Calendar.getInstance()
        minCalendar.add(Calendar.YEAR, -120)
        datePicker.minDate = minCalendar.timeInMillis
        
        // Enable spinner mode for easier year selection (if supported)
        try {
            datePicker.calendarViewShown = false
            datePicker.spinnersShown = true
        } catch (e: Exception) {
            // Fallback to default if spinner mode is not available
        }
        
        // Set title for the date picker
        datePickerDialog.setTitle("Select Birth Date")
        datePickerDialog.setMessage("Choose month, day, and year")
        
        datePickerDialog.show()
    }
    

    
    private fun applySelectedDate(selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
        // Format the date
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        
        val formattedDate = "${monthNames[selectedMonth]} $selectedDay, $selectedYear"
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        
        // Calculate more accurate age
        var age = currentYear - selectedYear
        if (selectedMonth > currentMonth || (selectedMonth == currentMonth && selectedDay > currentDay)) {
            age-- // Haven't had birthday this year yet
        }
        
        // Apply the selected date with enhanced styling
        binding.dateDisplay.text = formattedDate
        binding.dateDisplay.setTextColor(getColor(R.color.primary))
        binding.dateDisplay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_light)))
        
        // Show age information
        val ageDisplay = findViewById<TextView>(R.id.ageDisplay)
        ageDisplay?.let {
            it.text = "Age: $age years old"
            it.visibility = android.view.View.VISIBLE
            it.setTextColor(getColor(R.color.primary))
        }
        
        // Also update the hidden spinners for compatibility
        binding.dateYear.setText(selectedYear.toString())
        
        // Set month spinner (selectedMonth is 0-based)
        val monthNamesArray = resources.getStringArray(R.array.monthName)
        if (selectedMonth < monthNamesArray.size - 1) {
            binding.dateMonth.setSelection(selectedMonth + 1) // +1 because spinner has "Month" as first item
        }
        
        // Set day spinner
        binding.dateDay.setSelection(selectedDay)
        
        // Show enhanced success message with animation
        val snackbar = Snackbar.make(
            binding.root,
            "✅ Birth date selected successfully!",
            Snackbar.LENGTH_LONG
        )
        snackbar.setBackgroundTint(getColor(R.color.primary))
        snackbar.setTextColor(getColor(R.color.white))
        snackbar.setAction("VIEW") {
            // Optional: Could scroll to show the date picker
        }
        snackbar.setActionTextColor(getColor(R.color.white))
        snackbar.show()
        
        // Add subtle animation to the date display
        binding.dateDisplay.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .withEndAction {
                binding.dateDisplay.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start()
            }
            .start()
    }
    
    private fun setupInputValidation() {
        // First name validation
        binding.firstName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateName(
                    binding.firstName.text?.toString(), 
                    "First name"
                )
                ValidationUtils.applyValidationToEditText(binding.firstName, validation)
            }
        }
        
        // Last name validation
        binding.lastName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateName(
                    binding.lastName.text?.toString(), 
                    "Last name"
                )
                ValidationUtils.applyValidationToEditText(binding.lastName, validation)
            }
        }
        
        // Nickname validation
        binding.nickName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateNickname(
                    binding.nickName.text?.toString()
                )
                ValidationUtils.applyValidationToEditText(binding.nickName, validation)
            }
        }
        
        // Friend call validation
        binding.friendCall.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateTextField(
                    binding.friendCall.text?.toString(),
                    "What friends call you",
                    minLength = 2,
                    maxLength = 30
                )
                ValidationUtils.applyValidationToEditText(binding.friendCall, validation)
            }
        }
        
        // Like to call validation
        binding.likeToCall.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateTextField(
                    binding.likeToCall.text?.toString(),
                    "What you like to be called",
                    minLength = 2,
                    maxLength = 30
                )
                ValidationUtils.applyValidationToEditText(binding.likeToCall, validation)
            }
        }
        
        // Email validation
        binding.emailAdd.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateEmail(
                    binding.emailAdd.text?.toString()
                )
                ValidationUtils.applyValidationToEditText(binding.emailAdd, validation)
            }
        }
        
        // Phone number validation
        binding.contactNo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validatePhoneNumber(
                    binding.contactNo.text?.toString()
                )
                ValidationUtils.applyValidationToEditText(binding.contactNo, validation)
            }
        }
        
        // Address validation
        binding.address.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val validation = ValidationUtils.validateAddress(
                    binding.address.text?.toString()
                )
                ValidationUtils.applyValidationToEditText(binding.address, validation)
            }
        }
    }

    private fun btnBackOnClickListener() {
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
    
    private fun populateFormFields() {
        // Populate fields if slamBook has existing data
        slamBook.firstName?.let { binding.firstName.setText(it) }
        slamBook.lastName?.let { binding.lastName.setText(it) }
        slamBook.nickName?.let { binding.nickName.setText(it) }
        slamBook.friendCallMe?.let { binding.friendCall.setText(it) }
        slamBook.likeToCallMe?.let { binding.likeToCall.setText(it) }
        slamBook.email?.let { binding.emailAdd.setText(it) }
        slamBook.contactNo?.let { binding.contactNo.setText(it) }
        slamBook.address?.let { binding.address.setText(it) }
        
        // Set gender spinner if data exists
        slamBook.gender?.let { gender ->
            val genderAdapter = binding.gender.adapter
            for (i in 0 until genderAdapter.count) {
                if (genderAdapter.getItem(i).toString() == gender) {
                    binding.gender.setSelection(i)
                    break
                }
            }
        }
        
        // Set status spinner if data exists
        slamBook.status?.let { status ->
            val statusAdapter = binding.status.adapter
            for (i in 0 until statusAdapter.count) {
                if (statusAdapter.getItem(i).toString() == status) {
                    binding.status.setSelection(i)
                    break
                }
            }
        }
        
        // Handle birth date display
        slamBook.birthDate?.let { birthDate ->
            if (birthDate.isNotEmpty() && birthDate != "Select Birth Date") {
                var formattedDate = birthDate
                var selectedYear = 0
                
                // If it's already in formatted form, use it
                if (birthDate.contains(",")) {
                    formattedDate = birthDate
                    // Extract year for age calculation
                    try {
                        val parts = birthDate.split(" ")
                        if (parts.size >= 3) {
                            selectedYear = parts[2].toInt()
                        }
                    } catch (e: Exception) {
                        // Ignore parsing errors
                    }
                } else {
                    // Convert from "YYYY-MM-DD" format to readable format
                    try {
                        val parts = birthDate.split("-")
                        if (parts.size == 3) {
                            val year = parts[0]
                            val monthIndex = parts[1].toInt() - 1
                            val day = parts[2]
                            selectedYear = year.toInt()
                            
                            val monthNames = arrayOf(
                                "January", "February", "March", "April", "May", "June",
                                "July", "August", "September", "October", "November", "December"
                            )
                            
                            if (monthIndex in 0..11) {
                                formattedDate = "${monthNames[monthIndex]} $day, $year"
                            }
                        }
                    } catch (e: Exception) {
                        // If parsing fails, just use the original date
                        formattedDate = birthDate
                    }
                }
                
                // Apply the formatted date with styling
                binding.dateDisplay.text = formattedDate
                binding.dateDisplay.setTextColor(getColor(R.color.primary))
                binding.dateDisplay.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(R.color.background_light)))
                
                // Calculate and show age if we have a valid year
                if (selectedYear > 0) {
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val age = currentYear - selectedYear
                    
                    val ageDisplay = findViewById<TextView>(R.id.ageDisplay)
                    ageDisplay?.let {
                        it.text = "Age: $age years old"
                        it.visibility = android.view.View.VISIBLE
                        it.setTextColor(getColor(R.color.primary))
                    }
                }
            }
        }
    }
}