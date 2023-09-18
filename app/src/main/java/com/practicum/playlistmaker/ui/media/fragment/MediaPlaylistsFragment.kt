package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.ui.media.view_model.MediaPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaPlaylistsFragment : Fragment() {

    private val playlistsViewModel: MediaPlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeholderMessage.text = requireArguments().getString(TEXT_KEY)
    }

    companion object {

        private const val TEXT_KEY = "text_key"
        fun newInstance(text: String) = MediaPlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT_KEY, text)
            }
        }
    }
}