package com.assignment.okpassignment.repository

import android.util.Log

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance("asia-south1")

    fun requestOtp(email: String): Task<String> {
        Log.d("AuthRepository", "requestOtp called for: $email")
        val data = hashMapOf(
            "email" to email
        )

        return functions
            .getHttpsCallable("requestOtp")
            .call(data)
            .continueWith { task ->
                if (!task.isSuccessful) {
                    Log.e("AuthRepository", "requestOtp failed", task.exception)
                    throw task.exception!!
                }
                val result = task.result?.data
                Log.d("AuthRepository", "requestOtp result: $result")
                if (result is Map<*, *>) {
                    (result["message"] as? String) ?: "OTP sent"
                } else {
                    "OTP sent"
                }
            }
    }

    fun verifyOtp(email: String, code: String): Task<String> {
        Log.d("AuthRepository", "verifyOtp called for: $email with code: $code")
        val data = hashMapOf(
            "email" to email,
            "code" to code
        )

        return functions
            .getHttpsCallable("verifyOtp")
            .call(data)
            .continueWith { task ->
                if (!task.isSuccessful) {
                    Log.e("AuthRepository", "verifyOtp failed", task.exception)
                    throw task.exception!!
                }
                val result = task.result?.data
                Log.d("AuthRepository", "verifyOtp result: $result")
                if (result is Map<*, *>) {
                    (result["token"] as? String) ?: throw Exception("Token not found in response")
                } else if (result is String) {
                    result
                } else {
                    throw Exception("Invalid response from server")
                }
            }
    }

    fun signInWithCustomToken(token: String): Task<AuthResult> {
        return auth.signInWithCustomToken(token)
    }
}
