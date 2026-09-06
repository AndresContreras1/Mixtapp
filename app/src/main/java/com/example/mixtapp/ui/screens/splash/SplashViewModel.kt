package com.example.mixtapp.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashState())
    val uiState: StateFlow<SplashState> = _uiState.asStateFlow()

    // No recibe parametros, asi que la verificacion se hace al crear el ViewModel
    init {
        checkUser()
    }

    private fun checkUser() {
        _uiState.update {
            it.copy(
                verificando = false,
                hayUsuario = authRepository.currentUser != null,
            )
        }
    }
}
