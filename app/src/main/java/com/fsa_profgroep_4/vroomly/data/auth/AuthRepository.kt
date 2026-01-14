package com.fsa_profgroep_4.vroomly.data.auth

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.LoginQuery
import com.example.rocketreserver.RegisterMutation
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import com.apollographql.apollo.api.Optional


private const val TAG = "AuthRepository"

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginQuery.Login>
    suspend fun register(
        firstName: String,
        middleName: String?,
        lastName: String,
        email: String,
        userName: String,
        dob: LocalDate,
        password: String
    ): Result<String>
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

    override suspend fun register(
        firstName: String,
        middleName: String?,
        lastName: String,
        email: String,
        userName: String,
        dob: LocalDate,
        password: String
    ): Result<String> {
        return runCatching {
            val response = withContext(Dispatchers.IO) {
                apolloClient.mutation(RegisterMutation(
                    firstname = firstName,
                    middleName = Optional.presentIfNotNull(middleName),
                    lastName = lastName,
                    email = email,
                    userName = userName,
                    dob = dob,
                    password = password
                )).execute()
            }

            if (response.hasErrors()) {
                val error = response.errors?.firstOrNull()
                val extensions = error?.extensions

                // Try to extract field-specific errors from extensions
                val fieldErrorsRaw = (extensions?.get("errors") as? Map<*, *>)
                val fieldErrors = fieldErrorsRaw?.mapNotNull { (key, value) ->
                    if (key is String && value is String) key to value else null
                }?.toMap()

                if (!fieldErrors.isNullOrEmpty()) {
                    throw ValidationException(
                        fieldErrors = fieldErrors,
                        message = error.message
                    )
                }

                val errorMsg = error?.message ?: "Unknown registration error"
                throw Exception(errorMsg)
            }

            val registerResponse = response.data?.registerUser ?: throw Exception("Register data is null")

            login(email = email, password = password)

            registerResponse
        }
    }
}

