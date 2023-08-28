package com.jhb.cameraML.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainScreenViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState(pageTitle = null))
    val uiState : StateFlow<MainScreenUiState> = _uiState

    fun toggleDarkMode(){
        _uiState.update {
            it.copy(
                darkMode = !it.darkMode
            )
        }
    }

    fun setTitle(title : String){
        _uiState.update {
            it.copy(
                pageTitle = title
            )
        }
    }

}