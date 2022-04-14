package tv.olaris.android.ui.movieDetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import tv.olaris.android.R
import tv.olaris.android.databinding.FragmentMovieDetailsBinding
import tv.olaris.android.ui.base.BaseFragment


private const val ARG_UUID = "uuid"
private const val ARG_SERVER_ID = "server_id"

class MovieDetails : BaseFragment<FragmentMovieDetailsBinding>(FragmentMovieDetailsBinding::inflate) {
    private var uuid: String? = null
    private var serverId: Int = 0
    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uuid = it.getString(ARG_UUID).toString()
            serverId = it.getInt(ARG_SERVER_ID)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBarMovieItem.visibility = View.VISIBLE


        viewModel.coverArtUrl.observe(viewLifecycleOwner){
            Glide.with(view.context).load(it).into(binding.imageMovieDetailsCovertArt)
        }

        // Can we somehow pass this image through the navigator so it doesn't have to be retrieved twice?
        viewModel.posterUrl.observe(viewLifecycleOwner){
            Glide.with(view.context).load(it).into(binding.imageMovieDetailsPostertArt)
        }

        viewModel.movie.observe(viewLifecycleOwner){
            binding.progressBarMovieItem.visibility = View.INVISIBLE

           // binding.layoutMovieDetails.visibility = View.VISIBLE
            val movie = it!!

            binding.textMovieDetailsMovieName.text = movie.title
            binding.textMovieDetailsYearAndRuntime.text = getString(R.string.movie_year_and_runtime, movie.year.toString(), movie.getRuntime().toString(), movie.getResolution())
            binding.textMovieDetailsOverview.text = movie.overview
            binding.textMovieDetailsFileName.text = movie.getFileName()
            binding.imageMovieDetailsPostertArt.transitionName = movie.uuid
            binding.textMovieDetailsResolution.text = movie.getResolution()


            binding.btnPlayContent.setOnClickListener{
                val uuid = movie.fileUUIDs.first()

                val action =
                    MovieDetailsDirections.actionMovieDetailsFragmentToFragmentFullScreenMediaPlayer(
                        uuid,
                        serverId,
                        movie.playtime.toInt(),
                        mediaUuid = movie.uuid
                    )
                findNavController().navigate(action)
            }
            binding.layoutMovieDetails.visibility = View.VISIBLE
        }

        viewModel.getMovie(uuid = uuid!!, serverId = serverId)
    }
}