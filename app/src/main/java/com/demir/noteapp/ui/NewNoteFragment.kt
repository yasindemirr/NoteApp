package com.demir.noteapp.ui


import Recorder
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
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
import com.demir.noteapp.R
import com.demir.noteapp.databinding.FragmentNewNoteBinding
import com.demir.noteapp.databinding.RecordingVoiceAlertDialogBinding
import com.demir.noteapp.helper.toast
import com.demir.noteapp.model.Note
import com.demir.noteapp.viewmodel.NotesViewModel
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter


class NewNoteFragment : Fragment() {

    private lateinit var binding:FragmentNewNoteBinding
    private var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var noteViewModel: NotesViewModel
    private lateinit var alertBuilder:AlertDialog.Builder
    private var byteArray:ByteArray?=null
    private lateinit var note:Note
    private lateinit var mView: View
    val handler=Handler()
    private lateinit var recorder: Recorder
    private lateinit var toolBar: Toolbar
    private var color="#FFFFFF"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewNoteBinding.inflate(
            layoutInflater,
            container,
            false
        )
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        askPermission()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).viewModel
        colorPick()
        mView = view
        selectImage()
        registerLauncher()
        toolBar = requireActivity().findViewById(R.id.toolbar)
        context?.let {
            recorder = Recorder(it)
        }
        voiceDialog()
        binding.savedVoice.setOnClickListener {

            if (recorder.file?.exists() == true) {
                recorder.playAudioFile(recorder.file!!.absolutePath)
                binding.savedVoice.setImageResource(recorder.test!!)

            } else {
                Toast.makeText(context, "File is Empty", Toast.LENGTH_SHORT).show()
            }
        }
        recorder. mediaPlayer.setOnCompletionListener {
            Handler().postDelayed({
                binding.savedVoice.setImageResource(R.drawable.play_icon)
            },  500L)

        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // İzinler verildiğinde yapılacak işlemler burada yazılabilir.
            } else {
                Toast.makeText(context, "Request Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun askPermission() {

        val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun voiceDialog() {
    binding.micro.setOnLongClickListener {
        val view =LayoutInflater.from(requireContext()).inflate(R.layout.recording_voice_alert_dialog,null)
        alertBuilder= AlertDialog.Builder(requireContext())
        alertBuilder.setView(view)
        val dialog=alertBuilder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var timeText=view.findViewById<TextView>(R.id.timerText)
        var startTime = System.currentTimeMillis()
        recorder.startRecording()
        handler.post(object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                timeText.text = formatTime(elapsedTime)
                handler.postDelayed(this, 1000)
            }
        })
        binding.micro.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    recorder.stopRecording()
                    handler.removeCallbacksAndMessages(null)
                    dialog.dismiss()
                    binding.micro.setOnTouchListener(null)
                    binding.savedVoice.visibility=View.VISIBLE
                }
            }
            true
        }
        true
    }
    }
    private fun colorPick() {
        binding.colorPicker.setOnClickListener {
            ColorPickerDialog.Builder(requireContext()).setTitle("Product Color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let{
                            color="#${Integer.toHexString(it.color).substring(2)}"
                            binding.cardView2.setCardBackgroundColor(Color.parseColor(color))

                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker,_->
                    colorPicker.dismiss()
                }.show()
        }
    }
    private fun saveNote(view: View) {
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()
        val date=SimpleDateFormat("d MMM yyyy HH:mm", Locale("tr","tr")).format(Date())

        if (noteTitle.isNotEmpty()) {
            if (selectedBitmap != null){
                val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)
                val outputStream = ByteArrayOutputStream()
                smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
                byteArray = outputStream.toByteArray()
            }
                 note = Note(0, noteTitle, noteBody,byteArray,color,date,recorder.file?.absolutePath)
            noteViewModel.addNote(note)
            Snackbar.make(view,"Saved Succesfully",Snackbar.LENGTH_SHORT).show()
              view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)

        } else {
          //  activity?.toast("Please enter note title")
            Snackbar.make(view,"Please enter note title",Snackbar.LENGTH_SHORT).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_note, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                saveNote(mView)

            }
            else -> findNavController().navigateUp()
        }
        return true
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
    fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.selectedImage.setImageBitmap(selectedBitmap)
                            binding.selectedImage.visibility=View.VISIBLE

                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageData)
                            binding.selectedImage.setImageBitmap(selectedBitmap)
                            binding.selectedImage.visibility=View.VISIBLE
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

    fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
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

    fun formatTime(timeInMillis: Long): String {
        val seconds = timeInMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
       recorder.stopPlaying()
    }


}