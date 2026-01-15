package com.fsa_profgroep_4.vroomly.unit

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse.Builder
import com.apollographql.apollo.api.Error
import com.example.rocketreserver.LoginQuery
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepository
import com.fsa_profgroep_4.vroomly.data.auth.AuthRepositoryImpl
import com.fsa_profgroep_4.vroomly.data.local.AppDatabase
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AuthRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var apolloClient: ApolloClient
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = database.userDao()
        apolloClient = mockk()
        authRepository = AuthRepositoryImpl(apolloClient, userDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Test a successful login flow:
     * 1. API returns a valid user and token
     * 2. Repository returns a Success result
     * 3. User data is correctly persisted in the local Room database
     */
    @Test
    fun login_success_insertsUserIntoDatabase() = runBlocking {
        // Arrange: Prepare mock data and expected outcomes
        val email = "test@avans.nl"
        val password = "test1234"
        val userId = 1
        val token = "mock-token"

        val mockUser = LoginQuery.User(
            id = userId,
            email = email,
            firstname = "Test",
            middleName = null,
            lastname = "User",
            username = "testuser",
            dateOfBirth = LocalDate.Companion.parse("2000-01-01")
        )
        val mockLogin = LoginQuery.Login(
            token = token,
            user = mockUser
        )
        val mockData = LoginQuery.Data(login = mockLogin)

        val apolloResponse =
            Builder(operation = LoginQuery(email, password),
                requestUuid = UUID.randomUUID()
            ).data(
                data = mockData
            ).build()

        // Stub the ApolloClient to return our mock response when the login query is executed
        coEvery {
            apolloClient.query(any<LoginQuery>()).execute()
        } returns apolloResponse

        // Act: Execute the login function
        val result = authRepository.login(email, password)


        // Assert: Verify the repository result and database state
        // 1. Check if the function returned a success result with correct data
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(token, result.getOrNull()?.token)
        Assert.assertEquals(userId, result.getOrNull()?.user?.id)

        // 2. Check if the user was correctly saved to the local database using Turbine to test the Flow
        userDao.getCurrentUser().test {
            val userInDb = awaitItem()
            Assert.assertEquals(userId, userInDb?.id)
            Assert.assertEquals(email, userInDb?.email)
            Assert.assertEquals(token, userInDb?.token)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Test a failed login flow:
     * 1. API returns a GraphQL error
     * 2. Repository returns a Failure result with the correct error message
     * 3. Database is NOT modified (implicit)
     */
    @Test
    fun login_failure_returnsErrorResult() = runBlocking {
        // Arrange: Prepare a mock error response
        val email = "wrong@avans.nl"
        val password = "wrongpassword"
        val errorMessage = "Invalid credentials"

        val apolloResponse = Builder(
            operation = LoginQuery(email, password),
            requestUuid = UUID.randomUUID(),
        ).errors(listOf(Error.Builder(errorMessage).build()))
            .build()

        coEvery {
            apolloClient.query(any<LoginQuery>()).execute()
        } returns apolloResponse

        // Act: Execute the login function
        val result = authRepository.login(email, password)

        // Assert: Verify that the result is a failure containing the correct message
        Assert.assertTrue(result.isFailure)
        Assert.assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
