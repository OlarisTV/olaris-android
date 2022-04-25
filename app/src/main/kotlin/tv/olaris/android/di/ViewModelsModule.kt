package tv.olaris.android.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import tv.olaris.android.MainViewModel
import tv.olaris.android.ui.addServer.ServerViewModel
import tv.olaris.android.ui.dashboard.DashboardViewModel
import tv.olaris.android.ui.home.HomeViewModel
import tv.olaris.android.ui.mediaPlayer.MediaPlayerViewModel
import tv.olaris.android.ui.movieDetails.MovieDetailsViewModel
import tv.olaris.android.ui.movieLibrary.MovieLibraryViewModel
import tv.olaris.android.ui.showDetails.SeasonDetailsViewModel
import tv.olaris.android.ui.showDetails.ShowDetailsViewModel
import tv.olaris.android.ui.showLibrary.ShowLibraryViewModel

val viewModelsModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::ServerViewModel)
    viewModelOf(::ShowLibraryViewModel)
    viewModelOf(::ShowDetailsViewModel)
    viewModelOf(::SeasonDetailsViewModel)
    viewModelOf(::MovieLibraryViewModel)
    viewModelOf(::MovieDetailsViewModel)
    viewModelOf(::MediaPlayerViewModel)
}
