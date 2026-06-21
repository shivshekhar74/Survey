package com.example.myapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Screen 2 - Location & Details
    val state: String,
    val city: String,
    val villageName: String,
    val headName: String,
    val surveyorName: String,
    val mobileNo: String,
    val remark: String?,
    
    // Screen 3 - Survey Questions
    val lpgAvailable: Boolean,
    val fuelType: String,
    val woodConsumption: String,
    val numberOfCattle: String,
    val interestInBiogas: Boolean,
    val interestInPelletStove: Boolean,
    
    // Images (stored as file paths)
    val chulhaPhotoPath: String?,
    val woodStoragePhotoPath: String?,
    
    val isSynced: Boolean = false
)
