package com.example.mcpclient.data.api

interface ArthaApiRequest {
    val user_id: String
    val session_id: String
    val query: String
}

data class ArthaApiRequestImpl(
    override val user_id: String,
    override val session_id: String,
    override val query: String
) : ArthaApiRequest

interface ArthaApiResponse {
    val status: String
    val message: String
}

data class ArthaApiResponseImpl(
    override val status: String,
    override val message: String
) : ArthaApiResponse

interface ArthaApiService {
    /**
     * Send message to Artha API - the response will come through Firebase
     * @param userId - Phone number of the user
     * @param sessionId - Session ID (new or existing)
     * @param query - User's query
     * @returns ArthaApiResponse
     */
    suspend fun sendMessage(
        userId: String,
        sessionId: String,
        query: String
    ): Result<ArthaApiResponse>
    
    /**
     * Generate a new session ID
     * @returns String - New session ID
     */
    fun generateSessionId(): String
    
    /**
     * Check if session ID is valid format
     * @param sessionId - Session ID to validate
     * @returns Boolean
     */
    fun isValidSessionId(sessionId: String): Boolean
}
