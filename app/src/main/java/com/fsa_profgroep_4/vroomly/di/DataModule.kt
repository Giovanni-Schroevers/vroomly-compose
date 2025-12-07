package com.fsa_profgroep_4.vroomly.di

import com.apollographql.apollo.ApolloClient
import org.koin.dsl.module

val dataModule = module {
    single {
        ApolloClient.Builder()
            .serverUrl("https://dzxrnnq4til8g.cloudfront.net/graphql")
            .build()
    }
}