package com.fsa_profgroep_4.vroomly.data.network

import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import com.fsa_profgroep_4.vroomly.data.local.UserDao

class AuthorizationInterceptor(
    private val userDao: UserDao
) : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        val user = userDao.getUser()
        val token = user?.token

        val newRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        val response = chain.proceed(newRequest)

        if (response.statusCode == 401) {
            userDao.clearTable()
        }

        return response
    }
}
