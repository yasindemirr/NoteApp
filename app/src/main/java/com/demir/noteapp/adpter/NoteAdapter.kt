package com.demir.noteapp.adpter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.demir.noteapp.R
import com.demir.noteapp.databinding.NoteLayoutItemBinding
import com.demir.noteapp.model.Note
import com.demir.noteapp.ui.HomeFragmentDirections
import kotlin.random.Random

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutItemBinding,val context: Context) :
        RecyclerView.ViewHolder(itemBinding.root)
    //var colorsList :Array<String> = arrayOf( "#6C00FF","#C92C6D","#85CDFD","#FF9551","#A0D995","#F8B400","#BB6464","#DD4A48")


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
            ),parent.context
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
    //    val random = Random
        /*
        val color =
            Color.argb(
                255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256)
            )

         */
        val translate_anim = AnimationUtils.loadAnimation(holder.context, R.anim.trans_anim)


        holder.itemBinding.constraintLayout.setBackgroundColor(Color.parseColor(currentNote.colors))
        holder.itemBinding.tvDate.text=currentNote.date
        if (currentNote.audioFile!=null){
        holder.itemBinding.recordering.visibility=View.VISIBLE
        }else holder.itemBinding.recordering.visibility=View.GONE
        holder.itemView.setOnClickListener { view ->

            val direction = HomeFragmentDirections
                .actionHomeFragmentToUpdateNoteFragment(currentNote)
            view.findNavController().navigate(direction)
            holder.itemBinding.cardItem.startAnimation(translate_anim)


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}