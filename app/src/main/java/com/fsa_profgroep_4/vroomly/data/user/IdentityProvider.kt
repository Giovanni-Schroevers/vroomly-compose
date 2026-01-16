package com.fsa_profgroep_4.vroomly.data.user

import com.fsa_profgroep_4.vroomly.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

interface IdentityProvider {
    val currentUserFlow: Flow<UserEntity?>
    val currentUserIdFlow: Flow<Int?>
    suspend fun requireUserId(): Int
    suspend fun getUserIdOrNull(): Int?
}

class IdentityProviderImpl(
    private val userRepository: UserRepository
) : IdentityProvider {

    override val currentUserFlow: Flow<UserEntity?> = userRepository.getCurrentUser()

    override val currentUserIdFlow: Flow<Int?> =
        currentUserFlow.map { it?.id }

    override suspend fun requireUserId(): Int {
        return getUserIdOrNull() ?: throw IllegalStateException("No logged-in user found")
    }

    override suspend fun getUserIdOrNull(): Int? {
        var result: Int? = null
        currentUserFlow.filterNotNull().collect { user ->
            result = user.id
            return@collect
        }
        return result
    }
}