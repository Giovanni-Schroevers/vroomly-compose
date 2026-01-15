package com.fsa_profgroep_4.vroomly.data.network

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class ApolloAuthorizationInterceptor(
    private val userDao: UserDao
) : ApolloInterceptor {
    override fun <D : Operation.Data> intercept(
        request: com.apollographql.apollo.api.ApolloRequest<D>,
        chain: ApolloInterceptorChain
    ): Flow<ApolloResponse<D>> {
        return chain.proceed(request).onEach { response ->
            val hasAuthError = response.errors?.any { error ->
                val code = error.extensions?.get("code")
                val httpStatus = error.extensions?.get("httpStatus")
                code == "UNAUTHORIZED" || httpStatus == 401 || httpStatus == "401"
            } ?: false

            if (hasAuthError) {
                userDao.clearTable()
            }
        }
    }
}
