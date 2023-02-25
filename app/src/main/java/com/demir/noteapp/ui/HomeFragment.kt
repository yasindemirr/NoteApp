package com.demir.noteapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment

import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demir.noteapp.R
import com.demir.noteapp.adpter.NoteAdapter
import com.demir.noteapp.databinding.FragmentHomeBinding

import com.demir.noteapp.viewmodel.NotesViewModel


class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var noteAdapter: NoteAdapter
    lateinit var sharedPref: SharedPref
     var check:Boolean?=null


  //  private lateinit var dash:MenuItem
   // private lateinit var linear:MenuItem



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        sharedPref= SharedPref(requireContext().applicationContext)

        if (sharedPref.loadNightModeState()==true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.darkMode.setImageResource(R.drawable.ic_baseline_lightbulb_24)
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.darkMode.setImageResource(R.drawable.ic_baseline_nightlight_round_24)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).viewModel

        setUpRecyclerView()

        if (sharedPref.loadLayoutModeState()==true){
            grid()
        }else{
            linear()
        }

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }


       binding. darkMode.setOnClickListener {
           // Eğer uygulama karanlık moddayken butona tıklanırsa
           if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
               sharedPref.setNightModeState(false)
           }
           // Eğer uygulama aydınlık moddayken butona tıklanırsa
           else {
               // Arka plan rengini karanlık moda göre ayarla
               // Tercihleri kaydet
               AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
               sharedPref.setNightModeState(true)

           }
       }

    }

    private fun setUpRecyclerView() {

        binding.gridImage.setOnClickListener {
            grid()
            sharedPref.setLayoutModeState(true)
        }
        binding.linearImage.setOnClickListener {
            linear()
            sharedPref.setLayoutModeState(false)
        }
    }

    private fun linear(){
            noteAdapter = NoteAdapter()
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            binding.gridImage.visibility = View.VISIBLE
            binding.linearImage.visibility = View.GONE

            activity?.let {
                notesViewModel.getAllNotes().observe(viewLifecycleOwner, { note ->
                    noteAdapter.differ.submitList(note)
                    //updateUI(note)
                })
            }

    }

    private fun grid(){
            noteAdapter = NoteAdapter()
            binding.recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            binding.linearImage.visibility = View.VISIBLE
            binding.gridImage.visibility = View.GONE

            activity?.let {
                notesViewModel.getAllNotes().observe(viewLifecycleOwner, { note ->
                    noteAdapter.differ.submitList(note)
                    //updateUI(note)
                })
            }
    }
/*

    private fun updateUI(note: List<Note>) {
        if (note.isNotEmpty()) {
            binding.cardView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.cardView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

 */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)
        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
       // dash=menu.findItem(R.id.dashboard)
       // linear=menu.findItem(R.id.lineeeeer)
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)

    }
/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if (id==R.id.dashboard){
            binding.recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            dash.setVisible(false)
            linear.setVisible(true)
        }else{
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            linear.setVisible(false)
            dash.setVisible(true)

        }

        return true
    }

 */


    override fun onQueryTextSubmit(query: String?): Boolean {
        /*if (query != null) {
            searchNote(query)
        }*/
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText!=null){
            searchNote(newText)}
        return true
    }


    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(
            this, { list ->
                noteAdapter.differ.submitList(list)
            }
        )
    }
    fun restarApp(){
        val intent= Intent(requireContext().applicationContext,MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}