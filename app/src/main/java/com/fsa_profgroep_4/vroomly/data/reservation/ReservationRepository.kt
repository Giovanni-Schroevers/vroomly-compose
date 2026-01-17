package com.fsa_profgroep_4.vroomly.data.reservation

import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.GetReservationsByRenterIdQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ReservationRepository {
    suspend fun getReservationsByRenterId(renterId: Int): Result<List<GetReservationsByRenterIdQuery.GetReservationsByRenterId>>
}

class ReservationRepositoryImpl(
    private val apolloClient: ApolloClient
) : ReservationRepository {
    override suspend fun getReservationsByRenterId(renterId: Int): Result<List<GetReservationsByRenterIdQuery.GetReservationsByRenterId>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apolloClient.query(GetReservationsByRenterIdQuery(renterId)).execute()
            if (response.hasErrors()) {
                throw Exception(response.errors?.firstOrNull()?.message ?: "Unknown reservation error")
            }
            response.data?.getReservationsByRenterId ?: emptyList()
        }
    }
}
