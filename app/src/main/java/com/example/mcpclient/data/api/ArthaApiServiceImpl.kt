package com.example.mcpclient.data.api

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ArthaApiServiceImpl : ArthaApiService {
    
    private val baseUrl = "https://adityachaudhary2913-agent-artha.hf.space"
    private val endpoint = "/start/"
    private val gson = Gson()
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    companion object {
        private const val TAG = "ArthaApiService"
    }
    
    override suspend fun sendMessage(
        userId: String,
        sessionId: String,
        query: String
    ): Result<ArthaApiResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Sending message to Artha API: userId=$userId, sessionId=$sessionId")
            Log.d(TAG, "Query preview: ${query.take(100)}${if (query.length > 100) "..." else ""}")
            
            val requestBody = ArthaApiRequestImpl(
                user_id = userId,
                session_id = sessionId,
                query = query
            )
            
            val jsonBody = gson.toJson(requestBody)
            Log.d(TAG, "Request body: $jsonBody")
            
            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e(TAG, "Artha API error: ${response.code} - ${response.message}")
                Log.e(TAG, "Error body: $errorBody")
                return@withContext Result.failure(
                    Exception("API call failed: ${response.code} ${response.message}")
                )
            }
            
            val responseBody = response.body?.string() ?: ""
            Log.d(TAG, "Artha API response: $responseBody")
            
            val apiResponse = gson.fromJson(responseBody, ArthaApiResponseImpl::class.java)
            Log.d(TAG, "Parsed response: status=${apiResponse.status}, message=${apiResponse.message}")
            
            Result.success(apiResponse)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message to Artha API", e)
            Result.failure(e)
        }
    }
    
    override fun generateSessionId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1..15).map { ('a'..'z').random() }.joinToString("")
        return "session_${timestamp}_$random"
    }
    
    override fun isValidSessionId(sessionId: String): Boolean {
        return sessionId.startsWith("session_") && sessionId.length > 20
    }
}
