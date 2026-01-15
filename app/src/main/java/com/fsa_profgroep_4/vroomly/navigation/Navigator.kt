package com.fsa_profgroep_4.vroomly.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(startDestination: Any) {
    val backStack : SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: Any){
        if (backStack.lastOrNull() != destination) {
            backStack.add(destination)
        }
    }

    fun goBack(){
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun resetTo(destination: Any) {
        backStack.clear()
        backStack.add(destination)
    }
}
