package tv.olaris.android.ui.showDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import tv.olaris.android.models.Show


class SeasonPagerAdapter(fragment: Fragment, val show: Show, val serverId: Int) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
            return show.seasons.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val season = show.seasons[position]
        val fragment = SeasonDetailsFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("serverId", serverId)
            putString("seasonUUID", season.uuid)
        }
        return fragment
    }
}
