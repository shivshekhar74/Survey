package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class AttendanceReportResponse(
    @SerializedName("status") val status: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("fE_GetLoadDetails") val attendanceList: List<AttendanceRecord>?
)

data class AttendanceRecord(
    @SerializedName("punchDate") val punchDate: String,
    @SerializedName("punchIn") val punchIn: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("totalHours") val totalHours: String?,
    @SerializedName("punchInAddress") val punchInAddress: String?,
    @SerializedName("punchOutAddress") val punchOutAddress: String?
)

data class PunchResponse(
    @SerializedName("status") val status: String,
    @SerializedName("msg") val msg: String
)
