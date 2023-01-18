package com.demir.noteapp.adpter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.demir.noteapp.databinding.NoteLayoutItemBinding
import com.demir.noteapp.model.Note
import com.demir.noteapp.ui.HomeFragmentDirections
import kotlin.random.Random

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    private val differCallback =
        object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.noteBody == newItem.noteBody &&
                        oldItem.noteTitle == newItem.noteTitle
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.tvNoteTitle.text = currentNote.noteTitle
        holder.itemBinding.tvNoteBody.text = currentNote.noteBody
        if (currentNote.image==null){
            holder.itemBinding.circularImage.visibility= View.GONE
        }
        currentNote.image?.let {

            holder.itemBinding.circularImage.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    currentNote.image,
                    0,
                    currentNote.image.size
                )
            )
            //holder.binding.homeImage.visibility = View.VISIBLE
        }
        val random = Random
        val color =
            Color.argb(
                255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256)
            )
        holder.itemBinding.constraintLayout.setBackgroundColor(color)

        holder.itemView.setOnClickListener { view ->


            val direction = HomeFragmentDirections
                .actionHomeFragmentToUpdateNoteFragment(currentNote)
            view.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}