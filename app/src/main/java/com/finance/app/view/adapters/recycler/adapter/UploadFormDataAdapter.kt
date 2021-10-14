package com.finance.app.view.adapters.recycler.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.finance.app.R
import com.finance.app.databinding.ItemUploadFormBinding

class UploadFormDataAdapter(private val c: Context, private val imageList: ArrayList<Bitmap>) : RecyclerView.Adapter<UploadFormDataAdapter.MultipleImageViewHolder>() {
    private lateinit var binding: ItemUploadFormBinding

    override fun onBindViewHolder(holder: MultipleImageViewHolder, position: Int) {
        holder.bindItems(this.imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_upload_form, parent, false)
        return MultipleImageViewHolder(binding, c)
    }

    inner class MultipleImageViewHolder(val binding: ItemUploadFormBinding, val c: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(images: Bitmap) {
            binding.ivFormImage.setImageBitmap(images)
            binding.ivDeleteImage.setOnClickListener {
                deleteImage()
            }
        }

        private fun deleteImage() {
            imageList.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(adapterPosition,imageList.size)
            Toast.makeText(c, "Deleted", Toast.LENGTH_SHORT).show()
        }
    }
}
