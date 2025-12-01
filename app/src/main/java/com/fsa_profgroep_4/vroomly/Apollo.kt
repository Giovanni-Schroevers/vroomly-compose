package com.fsa_profgroep_4.vroomly

import com.apollographql.apollo.ApolloClient

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://dzxrnnq4til8g.cloudfront.net/graphql")
    .build()