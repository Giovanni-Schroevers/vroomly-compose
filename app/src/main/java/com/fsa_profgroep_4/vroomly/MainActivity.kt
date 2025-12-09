package com.fsa_profgroep_4.vroomly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import com.fsa_profgroep_4.vroomly.di.Navigator
import com.fsa_profgroep_4.vroomly.ui.theme.VroomlyTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope

@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope : Scope by activityRetainedScope()
    val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
