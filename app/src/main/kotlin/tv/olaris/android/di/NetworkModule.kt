package tv.olaris.android.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tv.olaris.android.repositories.OlarisGraphQLRepository
import tv.olaris.android.service.graphql.graphqlClient
import tv.olaris.android.service.http.OlarisHttpService
import tv.olaris.android.service.http.OlarisHttpServiceImpl
import tv.olaris.android.service.http.ktorClient
import tv.olaris.android.service.http.okHttpClient

val networkModule = module {
    singleOf(::ktorClient)
    singleOf(::OlarisHttpServiceImpl) { bind<OlarisHttpService>() }
    singleOf(::okHttpClient)
    singleOf(::graphqlClient)
    singleOf(::OlarisGraphQLRepository)
}
