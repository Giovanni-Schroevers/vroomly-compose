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
        Log.d(TAG, "login() called with email: $email")
        return runCatching {
            Log.d(TAG, "Executing Apollo query...")
            val response = withContext(Dispatchers.IO) {
                apolloClient.query(LoginQuery(email = email, password = password)).execute()
            }
            Log.d(TAG, "Apollo query executed. hasErrors: ${response.hasErrors()}")
            
            if (response.hasErrors()) {
                val errorMsg = response.errors?.first()?.message ?: "Unknown login error"
                Log.e(TAG, "Login error: $errorMsg")
                throw Exception(errorMsg)
            }

            val loginData = response.data?.login
            Log.d(TAG, "loginData: $loginData")
            
            if (loginData == null) {
                Log.e(TAG, "Login data is null")
                throw Exception("Login data is null")
            }
            
            val user = loginData.user
            Log.d(TAG, "user: $user, token: ${loginData.token?.take(20)}...")
            
            if (user != null && loginData.token != null) {
                Log.d(TAG, "Saving user to database...")
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
                Log.d(TAG, "User saved successfully!")
            } else {
                Log.w(TAG, "User or token is null, not saving to database")
            }
            
            loginData
        }.also { result ->
            result.onFailure { e ->
                Log.e(TAG, "login() failed with exception", e)
            }
        }
    }
}

