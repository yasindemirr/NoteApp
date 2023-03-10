package com.demir.noteapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String?,
    val noteBody: String?,
    val image:ByteArray?,
    val colors:String?=null,
    val date:String?,
    val audioFile: String?
) : Parcelable {
}


