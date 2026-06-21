package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") val status: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("employeeLogin") val employeeLogin: List<Employee>?
)

data class Employee(
    @SerializedName("empID") val empID: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("userimage") val userimage: String?,
    @SerializedName("employeeNumber") val employeeNumber: String,
    @SerializedName("isTenantAdmin") val isTenantAdmin: Int,
    @SerializedName("userImagePath") val userImagePath: String?,
    @SerializedName("tenantName") val tenantName: String?,
    @SerializedName("dateOfJoining") val dateOfJoining: String?,
    @SerializedName("street") val street: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("zipCode") val zipCode: String?,
    @SerializedName("newPassword") val newPassword: String?
)
