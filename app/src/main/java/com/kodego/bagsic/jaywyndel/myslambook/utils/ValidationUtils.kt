package com.kodego.diangca.ebrahim.myslambook.utils

import android.util.Patterns
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

object ValidationUtils {
    
    // Validation result class
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    // Name validation
    fun validateName(name: String?, fieldName: String = "Name"): ValidationResult {
        return when {
            name.isNullOrBlank() -> ValidationResult(false, "$fieldName is required")
            name.length < 2 -> ValidationResult(false, "$fieldName must be at least 2 characters")
            name.length > 50 -> ValidationResult(false, "$fieldName must not exceed 50 characters")
            !name.matches(Regex("^[a-zA-Z\\s'-]+$")) -> ValidationResult(false, "$fieldName can only contain letters, spaces, hyphens, and apostrophes")
            else -> ValidationResult(true)
        }
    }
    
    // Email validation
    fun validateEmail(email: String?): ValidationResult {
        return when {
            email.isNullOrBlank() -> ValidationResult(false, "Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(false, "Please enter a valid email address")
            email.length > 100 -> ValidationResult(false, "Email must not exceed 100 characters")
            else -> ValidationResult(true)
        }
    }
    
    // Phone number validation
    fun validatePhoneNumber(phone: String?): ValidationResult {
        return when {
            phone.isNullOrBlank() -> ValidationResult(false, "Phone number is required")
            phone.length < 10 -> ValidationResult(false, "Phone number must be at least 10 digits")
            phone.length > 15 -> ValidationResult(false, "Phone number must not exceed 15 digits")
            !phone.matches(Regex("^[+]?[0-9\\s()-]+$")) -> ValidationResult(false, "Please enter a valid phone number")
            else -> ValidationResult(true)
        }
    }
    
    // Address validation
    fun validateAddress(address: String?): ValidationResult {
        return when {
            address.isNullOrBlank() -> ValidationResult(false, "Address is required")
            address.length < 10 -> ValidationResult(false, "Please provide a complete address (at least 10 characters)")
            address.length > 200 -> ValidationResult(false, "Address must not exceed 200 characters")
            else -> ValidationResult(true)
        }
    }
    
    // Nickname validation
    fun validateNickname(nickname: String?): ValidationResult {
        return when {
            nickname.isNullOrBlank() -> ValidationResult(false, "Nickname is required")
            nickname.length < 2 -> ValidationResult(false, "Nickname must be at least 2 characters")
            nickname.length > 30 -> ValidationResult(false, "Nickname must not exceed 30 characters")
            else -> ValidationResult(true)
        }
    }
    
    // Generic text field validation
    fun validateTextField(text: String?, fieldName: String, minLength: Int = 1, maxLength: Int = 100, isRequired: Boolean = true): ValidationResult {
        return when {
            isRequired && text.isNullOrBlank() -> ValidationResult(false, "$fieldName is required")
            !isRequired && text.isNullOrBlank() -> ValidationResult(true)
            text!!.length < minLength -> ValidationResult(false, "$fieldName must be at least $minLength characters")
            text.length > maxLength -> ValidationResult(false, "$fieldName must not exceed $maxLength characters")
            else -> ValidationResult(true)
        }
    }
    
    // Spinner validation
    fun validateSpinner(spinner: Spinner, fieldName: String): ValidationResult {
        return when {
            spinner.selectedItemPosition == 0 -> ValidationResult(false, "Please select $fieldName")
            else -> ValidationResult(true)
        }
    }
    
    // Date validation
    fun validateDate(dateText: String?, defaultText: String = "Tap to select your birth date"): ValidationResult {
        return when {
            dateText.isNullOrBlank() || dateText == defaultText -> ValidationResult(false, "Please select your birth date")
            else -> ValidationResult(true)
        }
    }
    
    // Rating validation
    fun validateRating(rating: Float, fieldName: String = "Rating"): ValidationResult {
        return when {
            rating <= 0 -> ValidationResult(false, "Please provide a $fieldName")
            else -> ValidationResult(true)
        }
    }
    
    // List validation (for hobbies, songs, movies, skills)
    fun validateList(list: List<Any>?, fieldName: String, minItems: Int = 1): ValidationResult {
        return when {
            list.isNullOrEmpty() && minItems > 0 -> ValidationResult(false, "Please add at least $minItems $fieldName")
            list != null && list.size < minItems -> ValidationResult(false, "Please add at least $minItems $fieldName")
            else -> ValidationResult(true)
        }
    }
    
    // Helper function to apply validation to EditText
    fun applyValidationToEditText(editText: EditText, validation: ValidationResult) {
        if (!validation.isValid) {
            editText.error = validation.errorMessage
        } else {
            editText.error = null
        }
    }
    
    // Helper function to apply validation to TextInputEditText
    fun applyValidationToTextInputEditText(editText: TextInputEditText, validation: ValidationResult) {
        if (!validation.isValid) {
            editText.error = validation.errorMessage
        } else {
            editText.error = null
        }
    }
    
    // Comprehensive form validation
    class FormValidator {
        private val errors = mutableListOf<String>()
        
        fun validate(validation: ValidationResult, fieldName: String? = null): FormValidator {
            if (!validation.isValid) {
                val errorMsg = fieldName?.let { "$it: ${validation.errorMessage}" } ?: validation.errorMessage
                errorMsg?.let { errors.add(it) }
            }
            return this
        }
        
        fun isValid(): Boolean = errors.isEmpty()
        
        fun getErrors(): List<String> = errors.toList()
        
        fun getFirstError(): String? = errors.firstOrNull()
        
        fun getAllErrorsAsString(): String = errors.joinToString("\n")
    }
    
    // Age validation (if needed)
    fun validateAge(age: Int?): ValidationResult {
        return when {
            age == null -> ValidationResult(false, "Age is required")
            age < 1 -> ValidationResult(false, "Please enter a valid age")
            age > 150 -> ValidationResult(false, "Please enter a realistic age")
            else -> ValidationResult(true)
        }
    }
    
    // Password validation (if needed for future features)
    fun validatePassword(password: String?): ValidationResult {
        return when {
            password.isNullOrBlank() -> ValidationResult(false, "Password is required")
            password.length < 6 -> ValidationResult(false, "Password must be at least 6 characters")
            password.length > 50 -> ValidationResult(false, "Password must not exceed 50 characters")
            else -> ValidationResult(true)
        }
    }
    
    // URL validation (if needed for social media links)
    fun validateUrl(url: String?, fieldName: String = "URL"): ValidationResult {
        return when {
            url.isNullOrBlank() -> ValidationResult(true) // Optional field
            !Patterns.WEB_URL.matcher(url).matches() -> ValidationResult(false, "Please enter a valid $fieldName")
            else -> ValidationResult(true)
        }
    }
}