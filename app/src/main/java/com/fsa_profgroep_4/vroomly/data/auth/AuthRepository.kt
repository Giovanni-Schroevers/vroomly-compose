package com.fsa_profgroep_4.vroomly.data.auth

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.LoginQuery
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AuthRepository"

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginQuery.Login>
}

class AuthRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<LoginQuery.Login> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(LoginQuery(email = email, password = password)).execute()
            }
            
            if (response.hasErrors()) {
                val errorMsg = response.errors?.first()?.message ?: "Unknown login error"
                throw Exception(errorMsg)
            }

            val loginData = response.data?.login ?: throw Exception("Login data is null")

            val user = loginData.user

            userDao.clearTable()
            userDao.insertUser(
                UserEntity(
                    id = user.id,
                    email = user.email,
                    firstName = user.firstname,
                    middleName = user.middleName,
                    lastName = user.lastname,
                    username = user.username,
                    dateOfBirth = user.dateOfBirth.toString(),
                    token = loginData.token
                )
            )
            
            loginData
        }.also { result ->
            result.onFailure { e ->
                Log.e(TAG, "login() failed with exception", e)
            }
        }
    }
}

