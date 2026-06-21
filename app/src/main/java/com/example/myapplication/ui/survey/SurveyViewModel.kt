package com.example.myapplication.ui.survey

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.SurveyEntity
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.utils.FileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class SurveyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val surveyDao = db.surveyDao()

    // Form Data - Screen 2
    var state = MutableStateFlow("")
    var city = MutableStateFlow("")
    var villageName = MutableStateFlow("")
    var headName = MutableStateFlow("")
    var surveyorName = MutableStateFlow("")
    var mobileNo = MutableStateFlow("")
    var remark = MutableStateFlow("")

    // Form Data - Screen 3
    var lpgAvailable = MutableStateFlow(true)
    var fuelType = MutableStateFlow("")
    var woodConsumption = MutableStateFlow("")
    var numberOfCattle = MutableStateFlow("")
    var interestInBiogas = MutableStateFlow(true)
    var interestInPelletStove = MutableStateFlow(true)

    // Images
    var chulhaPhoto = MutableStateFlow<File?>(null)
    var woodStoragePhoto = MutableStateFlow<File?>(null)

    val savedSurveys: StateFlow<List<SurveyEntity>> = surveyDao.getAllSurveys()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<SurveyUiState>(SurveyUiState.Idle)
    val uiState: StateFlow<SurveyUiState> = _uiState.asStateFlow()

    fun submitSurvey() {
        viewModelScope.launch {
            _uiState.value = SurveyUiState.Loading
            
            val survey = SurveyEntity(
                state = state.value,
                city = city.value,
                villageName = villageName.value,
                headName = headName.value,
                surveyorName = surveyorName.value,
                mobileNo = mobileNo.value,
                remark = remark.value,
                lpgAvailable = lpgAvailable.value,
                fuelType = fuelType.value,
                woodConsumption = woodConsumption.value,
                numberOfCattle = numberOfCattle.value,
                interestInBiogas = interestInBiogas.value,
                interestInPelletStove = interestInPelletStove.value,
                chulhaPhotoPath = chulhaPhoto.value?.absolutePath,
                woodStoragePhotoPath = woodStoragePhoto.value?.absolutePath
            )

            // Save locally first
            surveyDao.insertSurvey(survey)
            
            // Mock Sync Logic
            try {
                // Here you would call your API, e.g.:
                // apiService.pushSurvey(survey.toMultipart())
                
                // If successful:
                surveyDao.deleteSyncedSurveys() 
                _uiState.value = SurveyUiState.Success("Survey uploaded successfully and local data cleared!")
            } catch (e: Exception) {
                _uiState.value = SurveyUiState.Success("Survey saved locally (Offline). It will sync when online.")
            }
        }
    }

    fun resetState() {
        _uiState.value = SurveyUiState.Idle
    }

    fun deleteSurvey(survey: SurveyEntity) {
        viewModelScope.launch {
            surveyDao.deleteSurvey(survey)
        }
    }

    fun syncSurvey(survey: SurveyEntity) {
        viewModelScope.launch {
            _uiState.value = SurveyUiState.Loading
            try {
                // Mock Sync Logic - Push to server
                // After successful push:
                val updatedSurvey = survey.copy(isSynced = true)
                surveyDao.updateSurvey(updatedSurvey)
                
                _uiState.value = SurveyUiState.Success("Survey synced to server successfully!")
            } catch (e: Exception) {
                _uiState.value = SurveyUiState.Error("Sync failed: ${e.localizedMessage}")
            }
        }
    }
}

sealed class SurveyUiState {
    object Idle : SurveyUiState()
    object Loading : SurveyUiState()
    data class Success(val message: String) : SurveyUiState()
    data class Error(val message: String) : SurveyUiState()
}
