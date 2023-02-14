package com.demir.noteapp.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.demir.noteapp.R
import com.demir.noteapp.database.NoteDatabase
import com.demir.noteapp.databinding.ActivityMainBinding
import com.demir.noteapp.repository.NoteRepository
import com.demir.noteapp.viewmodel.NotesViewModel
import com.demir.noteapp.viewmodel.NotesViewModelProviderFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    lateinit var viewModel: NotesViewModel
    lateinit var sharedPref: SharedPref



    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPref=SharedPref(this)
        if (sharedPref.loadNightModeState()==true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)}

            super.onCreate(savedInstanceState)
             setTheme(R.style.Theme_NoteApp)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupViewModel()
    }

    private fun setupViewModel(){

        val notesRepository = NoteRepository(NoteDatabase(this))

        val viewModelProviderFactory = NotesViewModelProviderFactory(application, notesRepository)

        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NotesViewModel::class.java)

    }




}
