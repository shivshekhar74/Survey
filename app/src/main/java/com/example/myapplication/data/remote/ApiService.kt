package com.example.myapplication.data.remote

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.model.AttendanceReportResponse
import com.example.myapplication.data.model.LoginResponse
import com.example.myapplication.data.model.PunchResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("api/HRMS/OKR_EmployeeLoginAPI")
    suspend fun login(
        @Query("UserID") userId: String,
        @Query("Password") password: String
    ): Response<LoginResponse>

    @GET("api/HRMS/OK_EmployeeAttendenceReport")
    suspend fun getAttendanceReport(
        @Query("EmpID") empId: Int
    ): Response<AttendanceReportResponse>

    @Multipart
    @POST("api/HRMS/OA_PunchInEmployee")
    suspend fun punchIn(
        @Query("EmpID") empId: Int,
        @Query("CurrentLocation") location: String,
        @Query("CurrLat") lat: Double,
        @Query("CurrLong") long: Double,
        @Part image: MultipartBody.Part?
    ): Response<PunchResponse>

    @Multipart
    @POST("api/HRMS/OA_PunchOutEmployee")
    suspend fun punchOut(
        @Part image: MultipartBody.Part?
    ): Response<PunchResponse>

    companion object {
        const val BASE_URL = BuildConfig.BASE_URL
    }
}
