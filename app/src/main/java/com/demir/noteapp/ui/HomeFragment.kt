package com.demir.noteapp.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.hardware.camera2.params.ColorSpaceTransform
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demir.noteapp.R
import com.demir.noteapp.adpter.NoteAdapter
import com.demir.noteapp.databinding.FragmentHomeBinding
import com.demir.noteapp.model.Note
import com.demir.noteapp.viewmodel.NoteViewModel
class HomeFragment : Fragment(R.layout.fragment_home),
    SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    lateinit var sharedPref: SharedPref

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        sharedPref= SharedPref(requireContext().applicationContext)
        if (sharedPref.loadLayoutModeState()==true){
            linear()
        }else{
            grid()
        }

        setUpRecyclerView()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }


        if (sharedPref.loadNightModeState() == true) {
            binding.switch1.isChecked = true
        }
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sharedPref.setNightModeState(true)
                restarApp()
                binding.switch1.text="On"

            } else {
                sharedPref.setNightModeState(false)
                restarApp()
                binding.switch1.text="Off"
            }
        }

    }
    private fun setUpLayout(){
        if(sharedPref.loadLayoutModeState()==false){
            sharedPref.setLayoutModeState(true)
          linear()
        }else{
            sharedPref.setLayoutModeState(false)
            grid()
        }
        if (sharedPref.loadLayoutModeState()==false){
            grid()
        }else{
            linear()
        }
    }

    private fun setUpRecyclerView() {
        setUpLayout()
        /*
        grid()
        linear()
        noteAdapter = NoteAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
        }

         */

        activity?.let {
            notesViewModel.getAllNote().observe(viewLifecycleOwner, { note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            })
        }

    }

    private fun linear() {
        noteAdapter = NoteAdapter()
        binding.linearImage.setOnClickListener {
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL, false
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            binding.gridImage.visibility=View.VISIBLE
            binding.linearImage.visibility=View.GONE
        }

    }

    private fun grid() {
        noteAdapter = NoteAdapter()
        binding.gridImage.setOnClickListener {
            binding.recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
                setHasFixedSize(true)
                adapter = noteAdapter
            }
            binding.linearImage.visibility=View.VISIBLE
            binding.gridImage.visibility=View.GONE
        }
    }


    private fun updateUI(note: List<Note>) {
        if (note.isNotEmpty()) {
            binding.cardView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.cardView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

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

        if (newText != null) {
            searchNote(newText)
        }
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



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun restarApp(){
        val intent= Intent(requireContext().applicationContext,MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()




    }
}