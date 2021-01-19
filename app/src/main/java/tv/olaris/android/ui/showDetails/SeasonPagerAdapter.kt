package tv.olaris.android.ui.showDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import tv.olaris.android.databases.Server
import tv.olaris.android.models.Show


class SeasonPagerAdapter(fragment: Fragment, s: Show, server: Server) : FragmentStateAdapter(fragment) {
    val show : Show = s
    val server: Server = server

    override fun getItemCount(): Int {
            return show.seasons.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val season = show.seasons[position]
        val fragment = SeasonDetailsFragment(season, server)
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_PARAM1, position + 1)
        }
        return fragment
    }
}