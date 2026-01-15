package com.fsa_profgroep_4.vroomly.data.user

import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.DeleteAccountMutation
import com.example.rocketreserver.EditUserMutation
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import com.fsa_profgroep_4.vroomly.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import com.apollographql.apollo.api.Optional

import com.fsa_profgroep_4.vroomly.data.auth.ValidationException

interface UserRepository {
    fun getCurrentUser(): Flow<UserEntity?>
    suspend fun updateUser(
        firstname: String?,
        middleName: String?,
        lastname: String?,
        username: String?,
        dob: LocalDate?
    ): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun logout()
}

class UserRepositoryImpl(
    private val apolloClient: ApolloClient,
    private val userDao: UserDao
) : UserRepository {

    override fun getCurrentUser(): Flow<UserEntity?> = userDao.getCurrentUser()

    override suspend fun updateUser(
        firstname: String?,
        middleName: String?,
        lastname: String?,
        username: String?,
        dob: LocalDate?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.mutation(
                EditUserMutation(
                    firstname = Optional.presentIfNotNull(firstname),
                    middleName = Optional.presentIfNotNull(middleName),
                    lastname = Optional.presentIfNotNull(lastname),
                    username = Optional.presentIfNotNull(username),
                    dob = Optional.presentIfNotNull(dob)
                )
            ).execute()

            if (response.hasErrors()) {
                val error = response.errors?.firstOrNull()
                val extensions = error?.extensions

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

                throw Exception(error?.message ?: "Unknown update error")
            }

            val updatedUser = response.data?.editUser?.user ?: throw Exception("Update failed: no data returned")

            val currentLocal = userDao.getUser()
            val token = currentLocal?.token ?: ""

            userDao.insertUser(
                UserEntity(
                    id = updatedUser.id,
                    email = updatedUser.email,
                    firstName = updatedUser.firstname,
                    middleName = updatedUser.middleName,
                    lastName = updatedUser.lastname,
                    username = updatedUser.username,
                    dateOfBirth = updatedUser.dateOfBirth.toString(),
                    token = token
                )
            )
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.mutation(DeleteAccountMutation()).execute()
            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown deletion error")
            }
            logout()
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            userDao.clearTable()
        }
    }
}
