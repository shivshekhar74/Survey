package com.example.myapplication.ui.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.local.SurveyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSurveysScreen(
    viewModel: SurveyViewModel,
    onBack: () -> Unit,
    onAddNew: () -> Unit
) {
    val savedSurveys by viewModel.savedSurveys.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = Color(0xFF1A47B2)

    if (uiState is SurveyUiState.Success) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            title = { Text("Sync Status") },
            text = { Text((uiState as SurveyUiState.Success).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetState() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Surveys", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNew,
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Survey")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.TopCenter
        ) {
            if (savedSurveys.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No saved surveys found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedSurveys) { survey ->
                        SurveyListItem(
                            survey = survey,
                            onDelete = { viewModel.deleteSurvey(survey) },
                            onSync = { viewModel.syncSurvey(survey) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurveyListItem(
    survey: SurveyEntity,
    onDelete: () -> Unit,
    onSync: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = survey.headName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A47B2)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (survey.isSynced) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Synced", fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE8F5E9))
                        )
                    } else {
                        IconButton(onClick = onSync) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Sync", tint = Color(0xFF1A47B2))
                        }
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Local", fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFFFF3E0))
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Location: ${survey.villageName}, ${survey.city}, ${survey.state}", fontSize = 14.sp)
            Text(text = "Surveyor: ${survey.surveyorName}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Mobile: ${survey.mobileNo}", fontSize = 14.sp, color = Color.Gray)
            
            if (!survey.remark.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Remark: ${survey.remark}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
