package com.example.myapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.PreferenceManager
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(
    private val repository: LoginRepository = LoginRepository(),
    private val preferenceManager: PreferenceManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var lastUserId: String = ""
    private var lastPassword: String = ""

    init {
        // Check if already logged in
        preferenceManager?.getEmployee()?.let {
            _uiState.value = LoginUiState.Success(LoginResponse("200", "Success", listOf(it)))
        }
    }

    fun login(userId: String, password: String) {
        lastUserId = userId
        lastPassword = password

        if (userId.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("User ID and Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = repository.login(userId, password)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val employee = loginResponse.employeeLogin?.firstOrNull()
                    
                    // Requirement: if empID = 0 then login success
                    if (loginResponse.status == "200" || (employee != null && employee.empID == 0)) {
                        employee?.let { preferenceManager?.saveEmployee(it) }
                        _uiState.value = LoginUiState.Success(loginResponse)
                    } else {
                        _uiState.value = LoginUiState.Error(loginResponse.msg)
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Login failed: ${response.message()}")
                }
            } catch (e: IOException) {
                _uiState.value = LoginUiState.Error("No Internet Connection. Please check your network.", isNetworkError = true)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        preferenceManager?.clearData()
        _uiState.value = LoginUiState.Idle
    }

    fun retry() {
        login(lastUserId, lastPassword)
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String, val isNetworkError: Boolean = false) : LoginUiState()
}
