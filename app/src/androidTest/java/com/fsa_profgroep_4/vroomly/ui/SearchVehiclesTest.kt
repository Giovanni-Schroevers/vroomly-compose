package com.fsa_profgroep_4.vroomly.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.type.VehicleFilterInput
import com.fsa_profgroep_4.vroomly.MainActivity
import com.fsa_profgroep_4.vroomly.data.vehicle.VehicleRepository
import com.fsa_profgroep_4.vroomly.ui.components.VehicleCardUi
import org.junit.Before
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class SearchVehiclesTest : KoinTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        loadKoinModules(
            module() {
                single<VehicleRepository> { FakeVehicleRepository() }
            }
        )
    }


    @Test
    fun searchVehicles_filtersList() {
        // Starts on Start/Login
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.onNodeWithText("Email *").performTextInput("test@avans.nl")
        composeTestRule.onNodeWithText("Password *").performTextInput("test1234")
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for app to load the home screen
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
        }

        // Navigate to Search using bottom bar tag
        composeTestRule.onNodeWithTag("bottom_nav_search").performClick()

        // Wait until vehicles list is visible
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithTag("vehicle_list").fetchSemanticsNodes().isNotEmpty()
        }

        // Type into search field
        composeTestRule.onNodeWithTag("vehicle_search_field").performTextInput("BMW")

        // Assert list exists
        composeTestRule.onNodeWithTag("vehicle_list").assertIsDisplayed()
    }
}

class FakeVehicleRepository : VehicleRepository {
    override suspend fun searchVehicles(
        filters: Optional<VehicleFilterInput>,
        paginationAmount: Int,
        paginationPage: Int
    ): Result<List<VehicleCardUi>> = Result.success(
        listOf(
            VehicleCardUi(
                imageUrl = "error",
                title = "BMW M3",
                location = "Rotterdam",
                owner = "Owner #1",
                tagText = "PETROL",
                badgeText = "4.7",
                costPerDay = 120.0
            ),
            VehicleCardUi(
                imageUrl = "error",
                title = "Audi A4",
                location = "Utrecht",
                owner = "Owner #2",
                tagText = "DIESEL",
                badgeText = "4.4",
                costPerDay = 80.0
            )
        )
    )
}
