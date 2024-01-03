package com.practicum.playlistmaker.ui.media.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.domain.media.model.NewPlaylistState
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.ui.media.view_model.MediaNewPlaylistViewModel
import com.practicum.playlistmaker.utils.Tools
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class MediaNewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigator: BottomNavigationView

    private val viewModel: MediaNewPlaylistViewModel by viewModel()
    private var coverPath: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Glide.with(requireContext())
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8)))
                    .into(binding.playlistCoverImage)
                saveImageToInternalStorage(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        bottomNavigator = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigator.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        viewModel.state.observe(viewLifecycleOwner) {
            if (it is NewPlaylistState.Empty) {
                binding.createButton.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.YP_Text_Gray)
                )
            } else {
                binding.createButton.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.YP_Blue)
                )
            }
        }

        binding.back.setOnClickListener {
            showDialog()
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch("image/*")
        }

        binding.createButton.setOnClickListener {

            if (binding.playlistTitleEt.text.toString().isNotEmpty()) {
                val playlist = Playlist(
                    playlistId = 0,
                    playlistTitle = binding.playlistTitleEt.text.toString(),
                    playlistCoverPath = coverPath,
                    playlistDescription = binding.playlistDescEt.text.toString(),
                    playlistTrackIds = emptyList(),
                    playlistTracksCount = 0
                )
                viewModel.addPlayList(playlist)

                Tools.showSnackbar(
                    binding.root,
                    getString(R.string.playlist_created, playlist.playlistTitle),
                    requireContext()
                )

                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        binding.playlistTitleEt.doOnTextChanged() { text, _, _, _ ->
            if (text.isNullOrEmpty()) viewModel.setEmptyState()
            else viewModel.setNotEmptyState()
        }

    }

    private fun saveImageToInternalStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.covers)
        )

        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, uri.lastPathSegment ?: "image")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        coverPath = file.toUri()
    }

    private fun showDialog() {
        if (binding.playlistTitleEt.text.toString()
                .isNotEmpty() || binding.playlistDescEt.text.toString()
                .isNotEmpty() || (coverPath != null)
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.question)
                .setMessage(R.string.warning)
                .setNeutralButton(R.string.cancel) { _, _ -> }
                .setNegativeButton(R.string.done) { _, _ ->
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .show()
        } else {
            findNavController().popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigator.visibility = View.VISIBLE
    }

}