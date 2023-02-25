package com.demir.noteapp.ui

import Recorder
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.demir.noteapp.R
import com.demir.noteapp.databinding.FragmentUpdateNoteBinding
import com.demir.noteapp.helper.toast
import com.demir.noteapp.model.Note
import com.demir.noteapp.viewmodel.NotesViewModel
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private lateinit var binding:FragmentUpdateNoteBinding
    private var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var byteArray:ByteArray?=null
    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var noteViewModel: NotesViewModel
    private lateinit var toolBar:Toolbar
    private lateinit var recorder: Recorder
    private var color=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateNoteBinding.inflate(
            layoutInflater,
            container,
            false
        )
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).viewModel
        currentNote = args.note!!
        colorPick()
        selectImage()
        registerLauncher()
        context?.let {
            recorder=Recorder(it)
        }

        binding.etNoteBodyUpdate.setText(currentNote.noteBody)
        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        currentNote.image?.let {
            binding.loadSelectedImage.setImageBitmap(BitmapFactory.decodeByteArray(currentNote.image,0,it.size))
        }
        binding.updtadeCardView.setCardBackgroundColor(Color.parseColor(currentNote.colors))
        if (currentNote.image != null){
            binding.loadSelectedImage.visibility=View.VISIBLE
        }
        if (currentNote.audioFile!=null){
            binding.savedVoice.visibility=View.VISIBLE
        }else{
            binding.savedVoice.visibility=View.GONE
        }
        binding.savedVoice.setOnClickListener {
            currentNote.audioFile?.let { it1 -> recorder.playAudioFile(it1) }
            binding.savedVoice.setImageResource(recorder.test!!)
        }
        recorder. mediaPlayer.setOnCompletionListener {
            Handler().postDelayed({
                binding.savedVoice.setImageResource(R.drawable.play_icon)
            },  500L)

        }


        binding.fabDone.setOnClickListener {
            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()
            val date=SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale("tr","tr")).format(Date())
            if(color.isEmpty()){
                color=currentNote.colors!!
            }
            if (title.isNotEmpty()) {
                if (selectedBitmap != null){
                    val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
                    val outputStream = ByteArrayOutputStream()
                    smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                    byteArray = outputStream.toByteArray()
                }else{
                    byteArray=currentNote.image
                }
            println(currentNote.audioFile)
                val note = Note(currentNote.id, title, body,byteArray,color,date,currentNote.audioFile)
                noteViewModel.updateNote(note)
                Snackbar.make(view,"Note Updated!",Snackbar.LENGTH_SHORT).show()
              //  activity?.toast("Note updated!")

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)

            } else {
                //activity?.toast("Enter a note title please")
                Snackbar.make(view,"Enter a note title please",Snackbar.LENGTH_SHORT).show()
            }
        }
        toolBar= requireActivity().findViewById(R.id.toolbar)

    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("Are you sure you want to permanently delete this note?")
            setPositiveButton("DELETE") { _, _ ->
                noteViewModel.deleteNote(currentNote)
                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_homeFragment
                )
            }
            setNegativeButton("CANCEL", null)
        }.create().show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_delete -> {
                deleteNote()
            }
            else->findNavController().navigateUp()
        }
        return true
    }
    private fun colorPick() {
        binding.updateColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext()).setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let{
                            color="#${Integer.toHexString(it.color).substring(2)}"
                            binding.updtadeCardView.setCardBackgroundColor(Color.parseColor(color))

                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker,_->
                    colorPicker.dismiss()
                }.show()
        }

    }
    private fun selectImage() {
        binding.imageControl.setOnClickListener{view->
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                        View.OnClickListener {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }
    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.loadSelectedImage.setImageBitmap(selectedBitmap)
                            binding.loadSelectedImage.visibility=View.VISIBLE

                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)
                            binding.loadSelectedImage.setImageBitmap(selectedBitmap)
                            binding.loadSelectedImage.visibility=View.VISIBLE
                        }
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }
    override fun onDestroy() {
        super.onDestroy()

    }

}