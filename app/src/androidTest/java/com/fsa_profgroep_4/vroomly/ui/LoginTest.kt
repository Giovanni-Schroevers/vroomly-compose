package com.fsa_profgroep_4.vroomly.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.fsa_profgroep_4.vroomly.MainActivity
import com.fsa_profgroep_4.vroomly.data.local.UserDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.inject

/**
 * UI / End-to-End Test for the Login functionality.
 * This test uses the real UI components and simulates user interaction 
 * from the starting screen all the way to the home screen.
 */
class LoginTest : KoinTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userDao: UserDao by inject()

    @Before
    fun setup() {
        runBlocking {
            userDao.clearTable()
        }
    }

    @Test
    fun loginSuccessTest() {
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }

        // 1. Navigate to Login screen from Start screen
        composeTestRule.onNodeWithText("Login").performClick()

        // 2. Fill in credentials
        composeTestRule.onNodeWithText("Email *").performTextInput("test@avans.nl")
        composeTestRule.onNodeWithText("Password *").performTextInput("test1234")

        // 3. Click the login button on the LoginScreen
        composeTestRule.onNodeWithText("Login").performClick()

        // 4. Verify we are on the Home screen
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}