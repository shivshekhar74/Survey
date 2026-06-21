package com.example.myapplication.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.AttendanceRecord
import com.example.myapplication.data.model.AttendanceReportResponse
import com.example.myapplication.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AttendanceViewModel(private val repository: AttendanceRepository = AttendanceRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val _attendanceList = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceList: StateFlow<List<AttendanceRecord>> = _attendanceList.asStateFlow()

    fun fetchAttendance(empId: Int) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val response = repository.getAttendanceReport(empId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.status == "200") {
                        _attendanceList.value = body.attendanceList ?: emptyList()
                        _uiState.value = AttendanceUiState.Success("Loaded")
                    } else {
                        _uiState.value = AttendanceUiState.Error(body.msg)
                    }
                } else {
                    _uiState.value = AttendanceUiState.Error("Failed to fetch report")
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun punchIn(empId: Int, location: String, lat: Double, long: Double, image: MultipartBody.Part?) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val response = repository.punchIn(empId, location, lat, long, image)
                if (response.isSuccessful && response.body()?.status == "200") {
                    _uiState.value = AttendanceUiState.Success(response.body()?.msg ?: "Punched In Successfully")
                    fetchAttendance(empId)
                } else {
                    _uiState.value = AttendanceUiState.Error(response.body()?.msg ?: "Punch In Failed")
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun punchOut(empId: Int, image: MultipartBody.Part?) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val response = repository.punchOut(image)
                if (response.isSuccessful && response.body()?.status == "200") {
                    _uiState.value = AttendanceUiState.Success(response.body()?.msg ?: "Punched Out Successfully")
                    fetchAttendance(empId)
                } else {
                    _uiState.value = AttendanceUiState.Error(response.body()?.msg ?: "Punch Out Failed")
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = AttendanceUiState.Idle
    }
}

sealed class AttendanceUiState {
    object Idle : AttendanceUiState()
    object Loading : AttendanceUiState()
    data class Success(val message: String) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}
