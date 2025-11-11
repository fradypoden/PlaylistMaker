package com.example.playlistmaker.media_library.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreateBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean -> }
    lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var _binding: FragmentPlaylistCreateBinding? = null
    private val binding get() = _binding!!
    lateinit var lastPicture: Uri
    lateinit var name: EditText
    lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.pickerImage.setImageURI(uri)
                    lastPicture = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNeutralButton("Отмена") { dialog, which ->
            }.setNegativeButton("Завершить") { dialog, which ->
                findNavController().navigateUp()
            }
        val description = binding.description
        val image = binding.pickerImage
        name = binding.name
        saveButton = binding.saveButton
        stateButton()

        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        binding.pickerImage.setOnClickListener {
            checkPermission()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            image.setBackgroundResource(0)
        }

        binding.name.doOnTextChanged { s, _, _, _ ->
            stateButton()
        }

        image.setImageResource(R.drawable.add_photo)
        val placeholder = R.drawable.add_photo

        binding.backButton.setOnClickListener {
            if ((name.text.isNullOrEmpty()) and (description.text.isNullOrEmpty()) and (placeholder == R.drawable.add_photo)) {
                findNavController().navigateUp()
            } else confirmDialog.show()
        }

        binding.saveButton.setOnClickListener {
            if (placeholder == R.drawable.add_photo) {
                Toast.makeText(requireContext(), "Плейлист создан", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                saveImageToPrivateStorage(lastPicture)
                Toast.makeText(requireContext(), "Плейлист создан", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun checkPermission() {
        val permissionProvided = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_MEDIA_IMAGES
        )
        if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
        } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(requireContext(), "Нет разрешения", Toast.LENGTH_LONG).show()
        }
    }

    private fun stateButton() {
        if (name.text.isNullOrEmpty()) {
            saveButton.isEnabled = false
            saveButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_grey
                )
            )
        } else {
            saveButton.isEnabled = true
            saveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "first_cover.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        val path = file.absolutePath.toString()
    }
}
