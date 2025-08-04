package com.example.mcpclient.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.mcpclient.data.api.JsonRpcParams
import com.example.mcpclient.data.api.JsonRpcRequest
import com.example.mcpclient.data.api.McpApiService
import com.example.mcpclient.data.models.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class McpRepository(
    private val apiService: McpApiService,
    private val context: Context
) {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("mcp_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val TAG = "McpRepository"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_SESSION_ID = "session_id"
        private const val SESSION_ID = "mcp-session-594e48ea-fea1-40ef-8c52-7552dd9272af"
    }

    fun isLoggedIn(): Boolean {
        return sharedPrefs.getString(KEY_PHONE_NUMBER, null) != null
    }

    fun getStoredPhoneNumber(): String? {
        return sharedPrefs.getString(KEY_PHONE_NUMBER, null)
    }

    fun logout() {
        sharedPrefs.edit()
            .remove(KEY_PHONE_NUMBER)
            .remove(KEY_SESSION_ID)
            .apply()
        Log.d(TAG, "User logged out")
    }

    suspend fun authenticate(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting authentication for phone: $phoneNumber")
            Log.d(TAG, "Using session ID: $SESSION_ID")

            // Step 1: Generate session ID - only check status code
            Log.d(TAG, "Step 1: Making GET request to mockWebPage")
            val sessionResponse = apiService.generateSessionId(SESSION_ID)

            Log.d(TAG, "Session response - Code: ${sessionResponse.code()}, Message: ${sessionResponse.message()}")

            if (!sessionResponse.isSuccessful) {
                Log.e(TAG, "Session generation failed: HTTP ${sessionResponse.code()} - ${sessionResponse.message()}")
                return@withContext Result.failure(Exception("Session generation failed: HTTP ${sessionResponse.code()}"))
            }

            Log.d(TAG, "Session generation successful: HTTP ${sessionResponse.code()}")

            // Step 2: Login simulation - only check status code
            Log.d(TAG, "Step 2: Making POST request to login with phone: $phoneNumber")
            val loginResponse = apiService.loginSim(SESSION_ID, phoneNumber)

            Log.d(TAG, "Login response - Code: ${loginResponse.code()}, Message: ${loginResponse.message()}")

            if (!loginResponse.isSuccessful) {
                Log.e(TAG, "Login failed: HTTP ${loginResponse.code()} - ${loginResponse.message()}")
                return@withContext Result.failure(Exception("Login failed: HTTP ${loginResponse.code()}"))
            }

            Log.d(TAG, "Login successful: HTTP ${loginResponse.code()}")

            // Store credentials
            sharedPrefs.edit()
                .putString(KEY_PHONE_NUMBER, phoneNumber)
                .putString(KEY_SESSION_ID, SESSION_ID)
                .apply()

            Log.d(TAG, "Authentication completed successfully")
            Result.success("Authentication successful")

        } catch (e: Exception) {
            Log.e(TAG, "Authentication error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchBankTransactions(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching bank transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_bank_transactions")
            )

            val response = apiService.fetchBankTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "Bank transactions fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch bank transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch bank transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bank transactions", e)
            Result.failure(e)
        }
    }

    suspend fun fetchNetWorth(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching net worth")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_net_worth")
            )

            val response = apiService.fetchNetWorth(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "Net worth fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch net worth: ${response.code()}")
                Result.failure(Exception("Failed to fetch net worth: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching net worth", e)
            Result.failure(e)
        }
    }

    suspend fun fetchEpfDetails(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching EPF details")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_epf_details")
            )

            val response = apiService.fetchEpfDetails(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "EPF details fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch EPF details: ${response.code()}")
                Result.failure(Exception("Failed to fetch EPF details: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching EPF details", e)
            Result.failure(e)
        }
    }

    suspend fun fetchCreditReport(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching credit report")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_credit_report")
            )

            val response = apiService.fetchCreditReport(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "Credit report fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch credit report: ${response.code()}")
                Result.failure(Exception("Failed to fetch credit report: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching credit report", e)
            Result.failure(e)
        }
    }

    suspend fun fetchMfTransactions(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching MF transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_mf_transactions")
            )

            val response = apiService.fetchMfTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "MF transactions fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch MF transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch MF transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching MF transactions", e)
            Result.failure(e)
        }
    }

    suspend fun fetchStockTransactions(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching stock transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_stock_transactions")
            )

            val response = apiService.fetchStockTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: "No data"
                Log.d(TAG, "Stock transactions fetched successfully")
                Result.success(responseText)
            } else {
                Log.e(TAG, "Failed to fetch stock transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch stock transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching stock transactions", e)
            Result.failure(e)
        }
    }

    // Parsed data methods
    suspend fun fetchBankTransactionsParsed(): Result<BankTransactionsResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed bank transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_bank_transactions")
            )

            val response = apiService.fetchBankTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, BankTransactionsResponse::class.java)
                        Log.d(TAG, "Bank transactions parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse bank transactions JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch bank transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch bank transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bank transactions", e)
            Result.failure(e)
        }
    }

    suspend fun fetchNetWorthParsed(): Result<NetWorthResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed net worth")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_net_worth")
            )

            val response = apiService.fetchNetWorth(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, NetWorthResponse::class.java)
                        Log.d(TAG, "Net worth parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse net worth JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch net worth: ${response.code()}")
                Result.failure(Exception("Failed to fetch net worth: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching net worth", e)
            Result.failure(e)
        }
    }

    suspend fun fetchEpfDetailsParsed(): Result<EpfDetailsResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed EPF details")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_epf_details")
            )

            val response = apiService.fetchEpfDetails(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, EpfDetailsResponse::class.java)
                        Log.d(TAG, "EPF details parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse EPF details JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch EPF details: ${response.code()}")
                Result.failure(Exception("Failed to fetch EPF details: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching EPF details", e)
            Result.failure(e)
        }
    }

    suspend fun fetchCreditReportParsed(): Result<CreditReportResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed credit report")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_credit_report")
            )

            val response = apiService.fetchCreditReport(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, CreditReportResponse::class.java)
                        Log.d(TAG, "Credit report parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse credit report JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch credit report: ${response.code()}")
                Result.failure(Exception("Failed to fetch credit report: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching credit report", e)
            Result.failure(e)
        }
    }

    suspend fun fetchMfTransactionsParsed(): Result<MfTransactionsResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed MF transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_mf_transactions")
            )

            val response = apiService.fetchMfTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, MfTransactionsResponse::class.java)
                        Log.d(TAG, "MF transactions parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse MF transactions JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch MF transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch MF transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching MF transactions", e)
            Result.failure(e)
        }
    }

    suspend fun fetchStockTransactionsParsed(): Result<StockTransactionsResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching parsed stock transactions")
            val request = JsonRpcRequest(
                params = JsonRpcParams(name = "fetch_stock_transactions")
            )

            val response = apiService.fetchStockTransactions(
                sessionId = SESSION_ID,
                body = request
            )

            if (response.isSuccessful) {
                val responseText = response.body()?.result?.content?.firstOrNull()?.text ?: ""
                Log.d(TAG, "Raw response: $responseText")
                
                if (responseText.isNotEmpty()) {
                    try {
                        val parsedData = gson.fromJson(responseText, StockTransactionsResponse::class.java)
                        Log.d(TAG, "Stock transactions parsed successfully")
                        Result.success(parsedData)
                    } catch (e: JsonSyntaxException) {
                        Log.e(TAG, "Failed to parse stock transactions JSON", e)
                        Result.failure(Exception("Failed to parse response: ${e.message}"))
                    }
                } else {
                    Result.failure(Exception("Empty response received"))
                }
            } else {
                Log.e(TAG, "Failed to fetch stock transactions: ${response.code()}")
                Result.failure(Exception("Failed to fetch stock transactions: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching stock transactions", e)
            Result.failure(e)
        }
    }
}
