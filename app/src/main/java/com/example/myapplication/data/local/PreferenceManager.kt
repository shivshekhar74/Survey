package com.example.myapplication.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.data.model.Employee
import com.google.gson.Gson

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_EMPLOYEE = "key_employee"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }

    fun saveEmployee(employee: Employee) {
        val employeeJson = gson.toJson(employee)
        sharedPreferences.edit().apply {
            putString(KEY_EMPLOYEE, employeeJson)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getEmployee(): Employee? {
        val employeeJson = sharedPreferences.getString(KEY_EMPLOYEE, null)
        return if (employeeJson != null) {
            gson.fromJson(employeeJson, Employee::class.java)
        } else {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}
