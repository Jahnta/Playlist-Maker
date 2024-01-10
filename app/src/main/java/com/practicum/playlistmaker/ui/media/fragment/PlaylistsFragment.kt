package com.practicum.playlistmaker.ui.media.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.ui.media.PlaylistsAdapter
import com.practicum.playlistmaker.ui.media.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistsBinding
    private lateinit var adapter: PlaylistsAdapter

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

        binding.placeholderButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_newPlaylistFragment)
        }
        binding.placeholderMessage.text = requireArguments().getString(TEXT_KEY)

        viewModel.getPlaylists()

        adapter = PlaylistsAdapter {
            val bundle = Bundle()
            bundle.putParcelable("playlist", it)
            findNavController().navigate(R.id.playlistDetailsFragment, bundle)
        }

        viewModel.playlists.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty())  {
                binding.placeholderMessage.visibility = View.VISIBLE
                binding.placeholderImage.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.placeholderMessage.visibility = View.GONE
                binding.placeholderImage.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.setItems(it)
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    companion object {

        private const val TEXT_KEY = "text_key"
        fun newInstance(text: String) = PlaylistsFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT_KEY, text)
            }
        }
    }
}