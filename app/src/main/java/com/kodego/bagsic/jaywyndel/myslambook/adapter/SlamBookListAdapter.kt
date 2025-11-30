package com.kodego.diangca.ebrahim.myslambook.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kodego.diangca.ebrahim.myslambook.R
import com.kodego.diangca.ebrahim.myslambook.databinding.ItemSlamBookBinding
import com.kodego.diangca.ebrahim.myslambook.model.SlamBook

class SlamBookListAdapter(
    private val slamBooks: List<SlamBook>,
    private val onDeleteClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit,
    private val onItemClick: (SlamBook) -> Unit
) : RecyclerView.Adapter<SlamBookListAdapter.SlamBookViewHolder>() {

    class SlamBookViewHolder(private val binding: ItemSlamBookBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(slamBook: SlamBook, onDeleteClick: (Int) -> Unit, onEditClick: (Int) -> Unit, onItemClick: (SlamBook) -> Unit) {
            binding.textName.text = "${slamBook.firstName ?: "Unknown"} ${slamBook.lastName ?: ""}"
            binding.textNickname.text = slamBook.nickName ?: "No nickname"
            binding.textEmail.text = slamBook.email ?: "No email"
            
            // Load profile picture
            if (!slamBook.profilePicUri.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse(slamBook.profilePicUri)
                    Glide.with(binding.imageProfile.context)
                        .load(uri)
                        .placeholder(R.drawable.profile_icon)
                        .error(R.drawable.profile_icon)
                        .circleCrop()
                        .into(binding.imageProfile)
                } catch (e: Exception) {
                    // If URI parsing fails, load default image
                    Glide.with(binding.imageProfile.context)
                        .load(R.drawable.profile_icon)
                        .circleCrop()
                        .into(binding.imageProfile)
                }
            } else {
                // Load default profile image
                Glide.with(binding.imageProfile.context)
                    .load(R.drawable.profile_icon)
                    .circleCrop()
                    .into(binding.imageProfile)
            }
            
            binding.btnDelete.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
            
            binding.btnEdit.setOnClickListener {
                onEditClick(adapterPosition)
            }
            
            // Add click listener for the entire item
            binding.root.setOnClickListener {
                onItemClick(slamBook)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlamBookViewHolder {
        val binding = ItemSlamBookBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return SlamBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlamBookViewHolder, position: Int) {
        holder.bind(slamBooks[position], onDeleteClick, onEditClick, onItemClick)
    }

    override fun getItemCount(): Int = slamBooks.size
}