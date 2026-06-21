package com.example.myapplication.data.repository

import com.example.myapplication.data.model.AttendanceReportResponse
import com.example.myapplication.data.model.PunchResponse
import com.example.myapplication.data.remote.ApiService
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AttendanceRepository {

    private val apiService: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    suspend fun getAttendanceReport(empId: Int): Response<AttendanceReportResponse> {
        return apiService.getAttendanceReport(empId)
    }

    suspend fun punchIn(
        empId: Int,
        location: String,
        lat: Double,
        long: Double,
        image: MultipartBody.Part?
    ): Response<PunchResponse> {
        return apiService.punchIn(empId, location, lat, long, image)
    }

    suspend fun punchOut(image: MultipartBody.Part?): Response<PunchResponse> {
        return apiService.punchOut(image)
    }
}
