package com.demir.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.demir.noteapp.model.Note
import com.demir.noteapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NotesViewModel(application: Application, private val notesRepository: NoteRepository): AndroidViewModel(application) {

    fun addNote(note: Note) = viewModelScope.launch {
        notesRepository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        notesRepository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }

    fun getAllNotes() = notesRepository.getAllNotes()

    fun searchNote(query: String?) = notesRepository.searchNote(query)

}