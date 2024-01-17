package com.practicum.playlistmaker.ui.media

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.media.fragment.FavouritesFragment
import com.practicum.playlistmaker.ui.media.fragment.PlaylistsFragment

class MediaViewPagerAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {

        var _context: Context? = null

        constructor(parentFragment: Fragment, c: Context) : this(parentFragment) {
            this._context = c
        }
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavouritesFragment.newInstance(_context!!.getString(R.string.favourites_is_empty))
            else -> PlaylistsFragment.newInstance(_context!!.getString(R.string.playlists_is_empty))
        }
    }

}