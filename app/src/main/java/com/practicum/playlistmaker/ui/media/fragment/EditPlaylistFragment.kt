package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.media.model.Playlist
import com.practicum.playlistmaker.ui.media.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.utils.Tools
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {
    private var playlist: Playlist? = null
    private val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = arguments?.getParcelable("playlist")
        coverPath = playlist?.playlistCoverPath
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.createButtonText.text = getString(R.string.save)
        binding.title.text = getString(R.string.edit)
        Glide.with(this)
            .load(coverPath)
            .placeholder(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_8)))
            .into(binding.playlistCoverImage)
        binding.playlistTitleEt.setText(playlist?.playlistTitle)
        binding.playlistDescEt.setText(playlist?.playlistDescription)


        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch("image/*")
        }
        binding.createButton.setOnClickListener {

            if (binding.playlistTitleEt.text.toString().isNotEmpty()) {
                val updatedPlaylist = Playlist(
                    playlistId = playlist!!.playlistId,
                    playlistTitle = binding.playlistTitleEt.text.toString(),
                    playlistCoverPath = coverPath,
                    playlistDescription = binding.playlistDescEt.text.toString(),
                    playlistTrackIds = playlist!!.playlistTrackIds,
                    playlistTracksCount = playlist!!.playlistTracksCount
                )
                viewModel.updatePlaylist(updatedPlaylist)

                Tools.showSnackbar(
                    binding.root,
                    getString(R.string.playlist_created, updatedPlaylist.playlistTitle),
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

}