package com.example.mcpclient.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcpclient.data.repository.McpRepository
import com.example.mcpclient.data.models.*
import kotlinx.coroutines.launch
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.GenericTypeIndicator
import com.example.mcpclient.data.models.UserChats
import com.example.mcpclient.data.models.ChatSession
import com.example.mcpclient.data.models.ChatMessage

class McpViewModel(private val repository: McpRepository) : ViewModel() {
    
    private val database = FirebaseDatabase.getInstance()

    var userChats by mutableStateOf<UserChats?>(null)
        private set

    var selectedChatSession by mutableStateOf<ChatSession?>(null)
        private set

    companion object {
        private const val TAG = "McpViewModel"
        
        val ALLOWED_PHONE_NUMBERS = listOf(
            "1414141414", "2222222222", "3333333333", "4444444444", "5555555555",
            "6666666666", "7777777777", "8888888888", "9999999999", "1010101010",
            "1212121212", "1313131313", "2020202020", "1515151515", "2121212121",
            "1616161616", "1717171717", "1818181818", "1919191919", "2525252525",
            "2222222222", "2323232323", "2424242424"
        )
        
        val API_ENDPOINTS = listOf(
            "Fetch Bank Transactions",
            "Fetch Net Worth",
            "Fetch EPF Details",
            "Fetch Credit Report",
            "Fetch MF Transactions",
            "Fetch Stock Transactions"
        )
    }

    fun getUserChats(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val userData = dataSnapshot.getValue(UserChats::class.java)
                userChats = userData ?: UserChats(userId = userId, chats = emptyMap())
                Log.d(TAG, "User chats loaded for userId: $userId")
            } else {
                // User doesn't exist in Firebase, create empty data
                userChats = UserChats(userId = userId, chats = emptyMap())
                Log.d(TAG, "User $userId doesn't exist in Firebase, created empty chats")
            }
        }.addOnFailureListener {
            Log.e(TAG, "Failed to load user chats", it)
            userChats = UserChats(userId = userId, chats = emptyMap()) // Fallback to empty
            errorMessage = "Failed to load user chats"
        }
    }

    fun getChatMessages(userId: String, sessionId: String) {
        val sessionRef = database.getReference("users").child(userId).child("chats").child(sessionId)
        sessionRef.get().addOnSuccessListener { dataSnapshot ->
            val messagesMap = dataSnapshot.getValue(object : GenericTypeIndicator<Map<String, ChatMessage>>() {})
            val session = ChatSession(sessionId = sessionId, messages = messagesMap ?: emptyMap())
            selectedChatSession = session
            Log.d(TAG, "Chat messages loaded for session: $sessionId")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to load chat messages", it)
            errorMessage = "Failed to load chat messages"
        }
    }

    fun observeUserChats(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserChats::class.java)
                    userChats = userData ?: UserChats(userId = userId, chats = emptyMap())
                    Log.d(TAG, "User chats updated for userId: $userId")
                } else {
                    // User doesn't exist, set empty data
                    userChats = UserChats(userId = userId, chats = emptyMap())
                    Log.d(TAG, "User $userId doesn't exist, set empty chats")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to observe user chats", error.toException())
                userChats = UserChats(userId = userId, chats = emptyMap()) // Fallback to empty
                errorMessage = "Failed to observe user chats"
            }
        })
    }

    fun observeChatMessages(userId: String, sessionId: String) {
        val sessionRef = database.getReference("users").child(userId).child("chats").child(sessionId)
        sessionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesMap = snapshot.getValue(object : GenericTypeIndicator<Map<String, ChatMessage>>() {})
                val session = ChatSession(sessionId = sessionId, messages = messagesMap ?: emptyMap())
                selectedChatSession = session
                Log.d(TAG, "Chat messages updated for session: $sessionId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to observe chat messages", error.toException())
                errorMessage = "Failed to observe chat messages"
            }
        })
    }

    fun sendMessage(userId: String, sessionId: String, message: String) {
        // Mock implementation - will be replaced with FastAPI call later
        Log.d(TAG, "Mock: Sending message '$message' to session: $sessionId for user: $userId")
        // TODO: Send to FastAPI server which will write to Firebase
    }
    
    var isLoggedIn by mutableStateOf(repository.isLoggedIn())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var responseData by mutableStateOf<String?>(null)
        private set
    
    var selectedPhoneNumber by mutableStateOf(ALLOWED_PHONE_NUMBERS[0])
        private set
    
    var selectedEndpoint by mutableStateOf(API_ENDPOINTS[0])
        private set
    
    // Parsed data states
    var netWorthData by mutableStateOf<NetWorthResponse?>(null)
        private set
    
    var bankTransactionsData by mutableStateOf<BankTransactionsResponse?>(null)
        private set
    
    var epfDetailsData by mutableStateOf<EpfDetailsResponse?>(null)
        private set
    
    var creditReportData by mutableStateOf<CreditReportResponse?>(null)
        private set
    
    var mfTransactionsData by mutableStateOf<MfTransactionsResponse?>(null)
        private set
    
    var stockTransactionsData by mutableStateOf<StockTransactionsResponse?>(null)
        private set
    
    var isDataLoading by mutableStateOf(false)
        private set
    
    init {
        Log.d(TAG, "ViewModel initialized. Is logged in: $isLoggedIn")
        if (isLoggedIn) {
            val storedPhone = repository.getStoredPhoneNumber()
            Log.d(TAG, "Stored phone number: $storedPhone")
        }
    }
    
    fun updateSelectedPhoneNumber(phoneNumber: String) {
        selectedPhoneNumber = phoneNumber
        Log.d(TAG, "Selected phone number updated: $phoneNumber")
    }
    
    fun updateSelectedEndpoint(endpoint: String) {
        selectedEndpoint = endpoint
        Log.d(TAG, "Selected endpoint updated: $endpoint")
    }
    
    fun clearError() {
        errorMessage = null
    }
    
    fun clearResponse() {
        responseData = null
    }
    
    fun signIn() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            Log.d(TAG, "Starting sign in process for: $selectedPhoneNumber")
            
            repository.authenticate(selectedPhoneNumber)
                .onSuccess { message ->
                    Log.d(TAG, "Sign in successful: $message")
                    isLoggedIn = true
                    isLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Sign in failed", exception)
                    errorMessage = exception.message ?: "Authentication failed"
                    isLoading = false
                }
        }
    }
    
    fun logout() {
        Log.d(TAG, "Logging out user")
        repository.logout()
        isLoggedIn = false
        responseData = null
        errorMessage = null
        
        // Clear Firebase chat data on logout
        userChats = null
        selectedChatSession = null
    }
    
    fun executeApiCall() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            responseData = null
            
            Log.d(TAG, "Executing API call: $selectedEndpoint")
            
            val result = when (selectedEndpoint) {
                "Fetch Bank Transactions" -> repository.fetchBankTransactions()
                "Fetch Net Worth" -> repository.fetchNetWorth()
                "Fetch EPF Details" -> repository.fetchEpfDetails()
                "Fetch Credit Report" -> repository.fetchCreditReport()
                "Fetch MF Transactions" -> repository.fetchMfTransactions()
                "Fetch Stock Transactions" -> repository.fetchStockTransactions()
                else -> Result.failure(Exception("Unknown endpoint"))
            }
            
            result
                .onSuccess { data ->
                    Log.d(TAG, "API call successful for: $selectedEndpoint")
                    responseData = data
                    isLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "API call failed for: $selectedEndpoint", exception)
                    errorMessage = exception.message ?: "API call failed"
                    isLoading = false
                }
        }
    }

    fun getStoredPhoneNumber(): String? {
        return repository.getStoredPhoneNumber()
    }
    
    // Methods to fetch parsed data
    fun fetchNetWorth() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching net worth data")
            
            repository.fetchNetWorthParsed()
                .onSuccess { data ->
                    Log.d(TAG, "Net worth data fetched successfully")
                    netWorthData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch net worth data", exception)
                    errorMessage = exception.message ?: "Failed to fetch net worth"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchBankTransactions() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching bank transactions data")
            
            repository.fetchBankTransactionsParsed()
                .onSuccess { data ->
                    Log.d(TAG, "Bank transactions data fetched successfully")
                    bankTransactionsData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch bank transactions data", exception)
                    errorMessage = exception.message ?: "Failed to fetch bank transactions"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchEpfDetails() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching EPF details data")
            
            repository.fetchEpfDetailsParsed()
                .onSuccess { data ->
                    Log.d(TAG, "EPF details data fetched successfully")
                    epfDetailsData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch EPF details data", exception)
                    errorMessage = exception.message ?: "Failed to fetch EPF details"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchCreditReport() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching credit report data")
            
            repository.fetchCreditReportParsed()
                .onSuccess { data ->
                    Log.d(TAG, "Credit report data fetched successfully")
                    creditReportData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch credit report data", exception)
                    errorMessage = exception.message ?: "Failed to fetch credit report"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchMfTransactions() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching MF transactions data")
            
            repository.fetchMfTransactionsParsed()
                .onSuccess { data ->
                    Log.d(TAG, "MF transactions data fetched successfully")
                    mfTransactionsData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch MF transactions data", exception)
                    errorMessage = exception.message ?: "Failed to fetch MF transactions"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchStockTransactions() {
        viewModelScope.launch {
            isDataLoading = true
            errorMessage = null
            
            Log.d(TAG, "Fetching stock transactions data")
            
            repository.fetchStockTransactionsParsed()
                .onSuccess { data ->
                    Log.d(TAG, "Stock transactions data fetched successfully")
                    stockTransactionsData = data
                    isDataLoading = false
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to fetch stock transactions data", exception)
                    errorMessage = exception.message ?: "Failed to fetch stock transactions"
                    isDataLoading = false
                }
        }
    }
    
    fun fetchAllData() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching all financial data")
            fetchNetWorth()
            fetchBankTransactions()
            fetchEpfDetails()
            fetchCreditReport()
            fetchMfTransactions()
            fetchStockTransactions()
        }
    }
}
