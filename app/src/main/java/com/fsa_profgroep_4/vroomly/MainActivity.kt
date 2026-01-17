package com.fsa_profgroep_4.vroomly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.ui.NavDisplay
import com.fsa_profgroep_4.vroomly.navigation.Home
import com.fsa_profgroep_4.vroomly.navigation.Start
import com.fsa_profgroep_4.vroomly.navigation.Navigator
import com.fsa_profgroep_4.vroomly.ui.theme.VroomlyTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityRetainedScope()
    private val navigator: Navigator by inject()
    private val mainViewModel: MainViewModel by inject()
    
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            mainViewModel.authState.collect { authState ->
                if (authState is AuthState.Loading) return@collect

                if (!isReady) {
                    if (authState is AuthState.Authenticated) {
                        navigator.resetTo(Home)
                    }
                    isReady = true
                } else {
                    if (authState is AuthState.Unauthenticated) {
                        navigator.resetTo(Start)
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            VroomlyTheme {
                Scaffold { paddingValues ->
                    NavDisplay(
                        backStack = navigator.backStack,
                        modifier = Modifier.padding(paddingValues),
                        onBack = { navigator.goBack() },
                        entryProvider = getEntryProvider()
                    )
                }
            }
        }
    }
}

