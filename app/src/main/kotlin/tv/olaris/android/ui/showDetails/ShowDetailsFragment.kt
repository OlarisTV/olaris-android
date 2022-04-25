package tv.olaris.android.ui.showDetails

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentShowDetailsBinding
import tv.olaris.android.ui.base.BaseFragment

private const val ARG_UUID = "uuid"

class ShowDetailsFragment :
    BaseFragment<FragmentShowDetailsBinding>(FragmentShowDetailsBinding::inflate) {
    private val showDetailsViewModel: ShowDetailsViewModel by viewModel()

    private var uuid: String? = null

    private lateinit var seasonPageAdapter: SeasonPagerAdapter
    private lateinit var viewPager: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uuid = it.getString(ARG_UUID).toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = this

        showDetailsViewModel.showDetails.observe(viewLifecycleOwner) { show ->
            show?.let {
                seasonPageAdapter = SeasonPagerAdapter(fragment, it)
                viewPager = view.findViewById(R.id.pager_show_detail_seasons)
                viewPager.adapter = seasonPageAdapter

                val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout_season_list)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = it.seasons[position].name
                }.attach()

                binding.progressBarShowItem.visibility = View.INVISIBLE
                binding.textShowDetailsAirDate.text = it.firstAirDate
                binding.textShowDetailsShowName.text = it.name
                binding.textShowDetailsOverview.text = it.overview

                val imageUrl = it.fullPosterUrl()
                Glide.with(view.context).load(imageUrl).into(binding.imageViewShowPoster)
                Glide.with(view.context).load(it.fullCoverArtUrl()).into(
                    binding.imageViewCoverArt
                )
            }
        }
        showDetailsViewModel.getShowDetails(uuid)
    }
}
