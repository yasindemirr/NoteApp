package com.demir.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demir.noteapp.repository.NoteRepository


class NotesViewModelProviderFactory(val app: Application, private val notesRepository: NoteRepository):
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(app,notesRepository) as T
    }

}