package com.kodego.diangca.ebrahim.myslambook.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class SlamBookDataManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("slam_book_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_SLAM_BOOKS = "slam_books"
    }
    
    fun saveSlamBook(slamBook: SlamBook) {
        val slamBooks = getAllSlamBooks().toMutableList()
        slamBooks.add(slamBook)
        saveSlamBooks(slamBooks)
    }
    
    fun getAllSlamBooks(): List<SlamBook> {
        val json = sharedPreferences.getString(KEY_SLAM_BOOKS, null)
        return if (json != null) {
            val type = object : TypeToken<List<SlamBook>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    
    fun updateSlamBook(index: Int, slamBook: SlamBook) {
        val slamBooks = getAllSlamBooks().toMutableList()
        if (index < slamBooks.size) {
            slamBooks[index] = slamBook
            saveSlamBooks(slamBooks)
        }
    }
    
    fun deleteSlamBook(index: Int) {
        val slamBooks = getAllSlamBooks().toMutableList()
        if (index < slamBooks.size) {
            slamBooks.removeAt(index)
            saveSlamBooks(slamBooks)
        }
    }
    
    private fun saveSlamBooks(slamBooks: List<SlamBook>) {
        val json = gson.toJson(slamBooks)
        sharedPreferences.edit().putString(KEY_SLAM_BOOKS, json).apply()
    }
    
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}