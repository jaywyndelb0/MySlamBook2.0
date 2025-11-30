package com.kodego.diangca.ebrahim.myslambook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kodego.diangca.ebrahim.myslambook.adapter.SlamBookListAdapter
import com.kodego.diangca.ebrahim.myslambook.data.SlamBookDataManager
import com.kodego.diangca.ebrahim.myslambook.databinding.ActivitySlamBookListBinding
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class SlamBookListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlamBookListBinding
    private lateinit var dataManager: SlamBookDataManager
    private lateinit var adapter: SlamBookListAdapter
    private var slamBooks: MutableList<SlamBook> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlamBookListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = SlamBookDataManager(this)
        setupRecyclerView()
        loadSlamBooks()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("slamBooK", SlamBook())
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSlamBooks()
    }

    private fun setupRecyclerView() {
        adapter = SlamBookListAdapter(
            slamBooks,
            onDeleteClick = { position ->
                showDeleteConfirmationDialog(position)
            },
            onEditClick = { position ->
                editSlamBook(position)
            },
            onItemClick = { slamBook ->
                openSlamBookDetails(slamBook)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadSlamBooks() {
        slamBooks.clear()
        slamBooks.addAll(dataManager.getAllSlamBooks())
        adapter.notifyDataSetChanged()

        if (slamBooks.isEmpty()) {
            binding.emptyStateLayout.visibility = android.view.View.VISIBLE
            binding.recyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateLayout.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val slamBook = slamBooks[position]
        AlertDialog.Builder(this)
            .setTitle("Delete Slam Book")
            .setMessage("Are you sure you want to delete \"${slamBook.firstName} ${slamBook.lastName}\"'s slam book? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteSlamBook(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSlamBook(position: Int) {
        dataManager.deleteSlamBook(position)
        loadSlamBooks()
        Snackbar.make(binding.root, "Slam book deleted", Snackbar.LENGTH_SHORT).show()
    }
    
    private fun editSlamBook(position: Int) {
        val slamBook = slamBooks[position]
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra("slamBooK", slamBook)
        intent.putExtra("edit_mode", true)
        intent.putExtra("edit_position", position)
        startActivity(intent)
    }
    
    private fun openSlamBookDetails(slamBook: SlamBook) {
        val intent = Intent(this, SlamBookDetailsActivity::class.java)
        intent.putExtra("slam_book", slamBook)
        startActivity(intent)
    }
}