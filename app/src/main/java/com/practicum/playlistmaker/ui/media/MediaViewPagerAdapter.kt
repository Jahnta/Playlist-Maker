package com.practicum.playlistmaker.ui.media

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.ui.media.fragment.FavouritesFragment
import com.practicum.playlistmaker.ui.media.fragment.PlaylistsFragment

class MediaViewPagerAdapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavouritesFragment.newInstance("Ваша медиатека пуста")
            else -> PlaylistsFragment.newInstance("Вы не создали ни одного плейлиста")
        }
    }
}