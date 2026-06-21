package com.example.myapplication.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyDao {
    @Insert
    suspend fun insertSurvey(survey: SurveyEntity)

    @Query("SELECT * FROM surveys WHERE isSynced = 0")
    fun getUnsyncedSurveys(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys")
    fun getAllSurveys(): Flow<List<SurveyEntity>>

    @Update
    suspend fun updateSurvey(survey: SurveyEntity)

    @Delete
    suspend fun deleteSurvey(survey: SurveyEntity)
    
    @Query("DELETE FROM surveys WHERE isSynced = 1")
    suspend fun deleteSyncedSurveys()
}
