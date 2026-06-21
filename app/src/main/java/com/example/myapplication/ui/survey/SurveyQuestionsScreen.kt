package com.example.myapplication.ui.survey

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.utils.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyQuestionsScreen(
    viewModel: SurveyViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val lpgAvailable by viewModel.lpgAvailable.collectAsState()
    val fuelType by viewModel.fuelType.collectAsState()
    val woodConsumption by viewModel.woodConsumption.collectAsState()
    val numberOfCattle by viewModel.numberOfCattle.collectAsState()
    val interestInBiogas by viewModel.interestInBiogas.collectAsState()
    val interestInPelletStove by viewModel.interestInPelletStove.collectAsState()
    val chulhaPhoto by viewModel.chulhaPhoto.collectAsState()
    val woodStoragePhoto by viewModel.woodStoragePhoto.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val primaryColor = Color(0xFF1A47B2)
    
    var tempFile by remember { mutableStateOf<File?>(null) }
    var isCapturingChulha by remember { mutableStateOf(true) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempFile != null) {
            if (isCapturingChulha) {
                viewModel.chulhaPhoto.value = tempFile
            } else {
                viewModel.woodStoragePhoto.value = tempFile
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = FileUtils.createImageFile(context)
            tempFile = file
            cameraLauncher.launch(FileUtils.getUriForFile(context, file))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Survey Questions", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.submitSurvey() }) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Survey Questions", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 18.sp)

                QuestionSection(number = 1, question = "LPG Available? *") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = lpgAvailable, onClick = { viewModel.lpgAvailable.value = true })
                        Text("Yes")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = !lpgAvailable, onClick = { viewModel.lpgAvailable.value = false })
                        Text("No")
                    }
                }

                QuestionSection(number = 2, question = "Fuel Type *") {
                    DropdownField(label = "Fuel Type", value = fuelType, onValueChange = { viewModel.fuelType.value = it }, options = listOf("Wood + LPG", "Wood Only", "LPG Only"))
                }

                QuestionSection(number = 3, question = "Wood Consumption (kg/day) *") {
                    OutlinedTextField(
                        value = woodConsumption,
                        onValueChange = { viewModel.woodConsumption.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                QuestionSection(number = 4, question = "Number of Cattle *") {
                    OutlinedTextField(
                        value = numberOfCattle,
                        onValueChange = { viewModel.numberOfCattle.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                QuestionSection(number = 5, question = "Interest in Biogas? *") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = interestInBiogas, onClick = { viewModel.interestInBiogas.value = true })
                        Text("Yes")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = !interestInBiogas, onClick = { viewModel.interestInBiogas.value = false })
                        Text("No")
                    }
                }

                QuestionSection(number = 6, question = "Interest in Pellet Stove? *") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = interestInPelletStove, onClick = { viewModel.interestInPelletStove.value = true })
                        Text("Yes")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = !interestInPelletStove, onClick = { viewModel.interestInPelletStove.value = false })
                        Text("No")
                    }
                }

                Text("Image Upload", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 18.sp)

                // Responsive image grid: 2 columns on most screens, but constrained max width
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ImagePicker(
                        label = "Chulha Photo *",
                        file = chulhaPhoto,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isCapturingChulha = true
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                val file = FileUtils.createImageFile(context)
                                tempFile = file
                                cameraLauncher.launch(FileUtils.getUriForFile(context, file))
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                    ImagePicker(
                        label = "Wood Storage Photo *",
                        file = woodStoragePhoto,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isCapturingChulha = false
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                val file = FileUtils.createImageFile(context)
                                tempFile = file
                                cameraLauncher.launch(FileUtils.getUriForFile(context, file))
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.submitSurvey() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState !is SurveyUiState.Loading
                ) {
                    if (uiState is SurveyUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit Survey", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        if (uiState is SurveyUiState.Success) {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Success") },
                text = { Text((uiState as SurveyUiState.Success).message) },
                confirmButton = {
                    TextButton(onClick = { 
                        viewModel.resetState()
                        onComplete() // Go back to list after success
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun QuestionSection(number: Int, question: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$number. $question", fontWeight = FontWeight.Medium)
        content()
    }
}

@Composable
fun ImagePicker(label: String, file: File?, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEEEEE))
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (file != null) {
                Image(
                    painter = rememberAsyncImagePainter(file),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color(0xFF1A47B2), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            } else {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
            }
        }
    }
}
