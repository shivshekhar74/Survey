package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.login.LoginUiState
import com.example.myapplication.ui.login.LoginViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import com.example.myapplication.ui.attendance.AttendanceScreen
import com.example.myapplication.ui.survey.LocationDetailsScreen
import com.example.myapplication.ui.survey.SurveyQuestionsScreen
import com.example.myapplication.ui.survey.SavedSurveysScreen
import com.example.myapplication.ui.survey.SurveyViewModel
import com.example.myapplication.data.local.PreferenceManager
import com.example.myapplication.data.repository.LoginRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val preferenceManager = remember { PreferenceManager(context) }
                
                val loginViewModel: LoginViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return LoginViewModel(
                                repository = LoginRepository(),
                                preferenceManager = preferenceManager
                            ) as T
                        }
                    }
                )
                
                val surveyViewModel: SurveyViewModel = viewModel()
                
                val loginUiState by loginViewModel.uiState.collectAsState()
                var currentScreen by remember { mutableStateOf("login") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when {
                        loginUiState is LoginUiState.Success && currentScreen == "login" -> {
                            currentScreen = "saved_surveys"
                        }
                    }

                    when (currentScreen) {
                        "login" -> LoginScreen(viewModel = loginViewModel)
                        "saved_surveys" -> SavedSurveysScreen(
                            viewModel = surveyViewModel,
                            onBack = { 
                                loginViewModel.logout()
                                currentScreen = "login"
                            },
                            onAddNew = { currentScreen = "survey_location" }
                        )
                        "attendance" -> {
                            val empId = (loginUiState as? LoginUiState.Success)?.data?.employeeLogin?.firstOrNull()?.empID ?: 0
                            AttendanceScreen(
                                empId = empId,
                                onBack = { currentScreen = "saved_surveys" }
                            )
                        }
                        "survey_location" -> LocationDetailsScreen(
                            viewModel = surveyViewModel,
                            onNext = { currentScreen = "survey_questions" },
                            onBack = { currentScreen = "saved_surveys" }
                        )
                        "survey_questions" -> SurveyQuestionsScreen(
                            viewModel = surveyViewModel,
                            onBack = { currentScreen = "survey_location" },
                            onComplete = { currentScreen = "saved_surveys" }
                        )
                    }
                }
            }
        }
    }
}