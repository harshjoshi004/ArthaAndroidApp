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
    
    var currentSessionId by mutableStateOf<String?>(null)
    
    var isWaitingForResponse by mutableStateOf(false)
        private set
    
    var isNewChat by mutableStateOf(false)
        private set
    
    var responseLoadingMessage by mutableStateOf<String?>(null)
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
            try {
                if (dataSnapshot.exists()) {
                    // Try to get as UserChats first
                    val userData = dataSnapshot.getValue(UserChats::class.java)
                    if (userData != null) {
                        userChats = userData
                        Log.d(TAG, "User chats loaded for userId: $userId")
                    } else {
                        // If direct deserialization fails, try manual parsing
                        val chatsMap = mutableMapOf<String, Map<String, ChatMessage>>()
                        val chatsChild = dataSnapshot.child("chats")
                        
                        for (sessionChild in chatsChild.children) {
                            val sessionId = sessionChild.key ?: continue
                            val messagesMap = mutableMapOf<String, ChatMessage>()
                            
                            for (messageChild in sessionChild.children) {
                                val messageId = messageChild.key ?: continue
                                try {
                                    val chatMessage = messageChild.getValue(ChatMessage::class.java)
                                    if (chatMessage != null) {
                                        messagesMap[messageId] = chatMessage.copy(id = messageId)
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to parse message $messageId in session $sessionId", e)
                                }
                            }
                            
                            if (messagesMap.isNotEmpty()) {
                                chatsMap[sessionId] = messagesMap
                            }
                        }
                        
                        val financialSummary = dataSnapshot.child("financial_summary").getValue(String::class.java) ?: ""
                        userChats = UserChats(userId = userId, chats = chatsMap, financial_summary = financialSummary)
                        Log.d(TAG, "User chats manually parsed for userId: $userId")
                    }
                } else {
                    // User doesn't exist in Firebase, create empty data
                    userChats = UserChats(userId = userId, chats = emptyMap())
                    Log.d(TAG, "User $userId doesn't exist in Firebase, created empty chats")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse user chats for $userId", e)
                userChats = UserChats(userId = userId, chats = emptyMap()) // Fallback to empty
                errorMessage = "Failed to load user chats"
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
            try {
                val messagesMap = mutableMapOf<String, ChatMessage>()
                
                // Safely parse each message from Firebase
                for (child in dataSnapshot.children) {
                    val messageId = child.key ?: continue
                    
                    try {
                        // Try to get as ChatMessage first
                        val chatMessage = child.getValue(ChatMessage::class.java)
                        if (chatMessage != null) {
                            messagesMap[messageId] = chatMessage.copy(id = messageId)
                        } else {
                            // If that fails, try to parse manually
                            val messageData = child.value
                            if (messageData is Map<*, *>) {
                                val llmResponse = (messageData["llm_response"] as? String) ?: ""
                                val queryUser = (messageData["query_user"] as? String) ?: ""
                                val timestamps = when (val ts = messageData["timestamps"]) {
                                    is Number -> ts.toLong()
                                    is String -> ts.toLongOrNull() ?: 0L
                                    else -> 0L
                                }
                                val llmThinking = (messageData["llm_thinking"] as? String) ?: ""
                                
                                val parsedMessage = ChatMessage(
                                    id = messageId,
                                    llm_response = llmResponse,
                                    query_user = queryUser,
                                    timestamps = timestamps,
                                    llm_thinking = llmThinking
                                )
                                messagesMap[messageId] = parsedMessage
                            } else if (messageData is String) {
                                // Handle case where Firebase contains just a string
                                Log.w(TAG, "Found string data instead of ChatMessage object for message $messageId: $messageData")
                                val defaultMessage = ChatMessage(
                                    id = messageId,
                                    llm_response = messageData,
                                    query_user = "",
                                    timestamps = System.currentTimeMillis(),
                                    llm_thinking = ""
                                )
                                messagesMap[messageId] = defaultMessage
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse message $messageId", e)
                        // Continue with other messages
                    }
                }
                
                val session = ChatSession(sessionId = sessionId, messages = messagesMap)
                selectedChatSession = session
                Log.d(TAG, "Chat messages loaded for session: $sessionId with ${messagesMap.size} messages")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to process chat messages for session $sessionId", e)
                selectedChatSession = ChatSession(sessionId = sessionId, messages = emptyMap())
                errorMessage = "Failed to load chat messages"
            }
        }.addOnFailureListener {
            Log.e(TAG, "Failed to load chat messages", it)
            errorMessage = "Failed to load chat messages"
        }
    }

    fun observeUserChats(userId: String) {
        val userRef = database.getReference("users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        // Try to get as UserChats first
                        val userData = snapshot.getValue(UserChats::class.java)
                        if (userData != null) {
                            userChats = userData
                            Log.d(TAG, "User chats updated for userId: $userId")
                        } else {
                            // If direct deserialization fails, try manual parsing
                            val chatsMap = mutableMapOf<String, Map<String, ChatMessage>>()
                            val chatsChild = snapshot.child("chats")
                            
                            for (sessionChild in chatsChild.children) {
                                val sessionId = sessionChild.key ?: continue
                                val messagesMap = mutableMapOf<String, ChatMessage>()
                                
                                for (messageChild in sessionChild.children) {
                                    val messageId = messageChild.key ?: continue
                                    try {
                                        val chatMessage = messageChild.getValue(ChatMessage::class.java)
                                        if (chatMessage != null) {
                                            messagesMap[messageId] = chatMessage.copy(id = messageId)
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Failed to parse message $messageId in session $sessionId", e)
                                    }
                                }
                                
                                if (messagesMap.isNotEmpty()) {
                                    chatsMap[sessionId] = messagesMap
                                }
                            }
                            
                            val financialSummary = snapshot.child("financial_summary").getValue(String::class.java) ?: ""
                            userChats = UserChats(userId = userId, chats = chatsMap, financial_summary = financialSummary)
                            Log.d(TAG, "User chats manually parsed for userId: $userId")
                        }
                    } else {
                        // User doesn't exist, set empty data
                        userChats = UserChats(userId = userId, chats = emptyMap())
                        Log.d(TAG, "User $userId doesn't exist, set empty chats")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to observe user chats for $userId", e)
                    userChats = UserChats(userId = userId, chats = emptyMap())
                    errorMessage = "Failed to observe user chats"
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
                    try {
                        val messagesMap = mutableMapOf<String, ChatMessage>()
                        
                        // Safely parse each message from Firebase
                        for (child in snapshot.children) {
                            val messageId = child.key ?: continue
                            
                            try {
                                // Try to get as ChatMessage first
                                val chatMessage = child.getValue(ChatMessage::class.java)
                                if (chatMessage != null) {
                                    messagesMap[messageId] = chatMessage.copy(id = messageId)
                                } else {
                                    // If that fails, try to parse manually
                                    val messageData = child.value
                                    if (messageData is Map<*, *>) {
                                        val llmResponse = (messageData["llm_response"] as? String) ?: ""
                                        val queryUser = (messageData["query_user"] as? String) ?: ""
                                        val timestamps = when (val ts = messageData["timestamps"]) {
                                            is Number -> ts.toLong()
                                            is String -> ts.toLongOrNull() ?: 0L
                                            else -> 0L
                                        }
                                        val llmThinking = (messageData["llm_thinking"] as? String) ?: ""
                                        
                                        val parsedMessage = ChatMessage(
                                            id = messageId,
                                            llm_response = llmResponse,
                                            query_user = queryUser,
                                            timestamps = timestamps,
                                            llm_thinking = llmThinking
                                        )
                                        messagesMap[messageId] = parsedMessage
                                    } else if (messageData is String) {
                                        // Handle case where Firebase contains just a string
                                        Log.w(TAG, "Found string data instead of ChatMessage object for message $messageId: $messageData")
                                        // Skip this message or create a default ChatMessage
                                        val defaultMessage = ChatMessage(
                                            id = messageId,
                                            llm_response = messageData,
                                            query_user = "",
                                            timestamps = System.currentTimeMillis(),
                                            llm_thinking = ""
                                        )
                                        messagesMap[messageId] = defaultMessage
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to parse message $messageId", e)
                                // Continue with other messages
                            }
                        }
                        
                        val allMessages = messagesMap.values.sortedBy { it.timestamps }
                        val session = ChatSession(sessionId = sessionId, messages = messagesMap)
                        selectedChatSession = session

                        // Handle thinking process
                        if (allMessages.any { it.isThinkingOnly }) {
                            Log.d(TAG, "Thinking phase detected")
                            isLoading = true
                            responseLoadingMessage = "Artha is thinking..."
                        } else {
                            isLoading = false
                            responseLoadingMessage = null
                        }

                        Log.d(TAG, "Chat messages updated for session: $sessionId with ${messagesMap.size} messages")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to process chat messages for session $sessionId", e)
                        // Set empty session to prevent crash
                        selectedChatSession = ChatSession(sessionId = sessionId, messages = emptyMap())
                        errorMessage = "Failed to load chat messages"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to observe chat messages", error.toException())
                    errorMessage = "Failed to observe chat messages"
                }
            })
        }

    fun sendMessage(userId: String? = repository.getStoredPhoneNumber(), messageText: String) {
        viewModelScope.launch {
            try {
                if (userId == null) {
                    errorMessage = "User ID not available!"
                    return@launch
                }
                
                // Use current session ID if available, otherwise create new one ONLY when explicitly needed
                val sessionId = currentSessionId ?: run {
                    Log.w(TAG, "No current session ID available, creating new session")
                    val newSessionId = repository.createNewChat().getOrThrow()
                    currentSessionId = newSessionId
                    
                    // Create empty session in Firebase
                    val chatRef = database.getReference("users").child(userId).child("chats").child(newSessionId)
                    chatRef.setValue(emptyMap<String, ChatMessage>())
                    
                    // Initialize empty session locally
                    selectedChatSession = ChatSession(sessionId = newSessionId, messages = emptyMap())
                    
                    newSessionId
                }
                
                Log.d(TAG, "Sending message '$messageText' to session: $sessionId for user: $userId")
                
                // Set loading states
                isWaitingForResponse = true
                
                // Send to Artha API
                repository.sendChatMessage(messageText, sessionId, userId)
                    .onSuccess { responseMessage ->
                        Log.d(TAG, "Message sent successfully. Response: $responseMessage")
                        
                        // The response from the Artha API will update Firebase
                        // We already observe Firebase changes in ChatDetailsScreen
                        
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to send message", exception)
                        errorMessage = "Failed to send message: ${exception.message}"
                        isWaitingForResponse = false
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in sendMessage", e)
                errorMessage = "Failed to send message: ${e.message}"
                isWaitingForResponse = false
            }
        }
    }
    
fun createNewChat(userId: String? = repository.getStoredPhoneNumber()) {
        currentSessionId = null
        isWaitingForResponse = false
        isNewChat = true
        viewModelScope.launch {
            try {
                if (userId == null) {
                    errorMessage = "User ID not available!"
                    return@launch
                }
                Log.d(TAG, "Creating new chat for user: $userId")
                
                repository.createNewChat()
                    .onSuccess { sessionId ->
                        Log.d(TAG, "New chat created with session ID: $sessionId")
                        
                        // Create the chat session in Firebase
                        val chatRef = database.getReference("users").child(userId).child("chats").child(sessionId)
                        chatRef.setValue(emptyMap<String, ChatMessage>())
                        
                        // Update local state
                        val newSession = ChatSession(sessionId = sessionId, messages = emptyMap())
                        selectedChatSession = newSession
                        
                        currentSessionId = sessionId
                        
                        // Refresh user chats to include the new session
                        getUserChats(userId)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to create new chat", exception)
                        errorMessage = "Failed to create new chat: ${exception.message}"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error in createNewChat", e)
                errorMessage = "Failed to create new chat: ${e.message}"
            }
        }
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
