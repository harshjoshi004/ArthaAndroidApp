package com.example.mcpclient.data.api

import com.example.mcpclient.data.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface McpApiService {

    @GET("mockWebPage")
    suspend fun generateSessionId(
        @Query("sessionId") sessionId: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("login")
    suspend fun loginSim(
        @Field("sessionId") sessionId: String,
        @Field("phoneNumber") phoneNumber: String
    ): Response<ResponseBody>

    @POST("mcp/stream")
    suspend fun fetchBankTransactions(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>

    @POST("mcp/stream")
    suspend fun fetchNetWorth(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>

    @POST("mcp/stream")
    suspend fun fetchEpfDetails(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>

    @POST("mcp/stream")
    suspend fun fetchCreditReport(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>

    @POST("mcp/stream")
    suspend fun fetchMfTransactions(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>

    @POST("mcp/stream")
    suspend fun fetchStockTransactions(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Mcp-Session-Id") sessionId: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse<JsonRpcContent>>
}

data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val id: Int = 1,
    val method: String = "tools/call",
    val params: JsonRpcParams
)

data class JsonRpcParams(
    val name: String,
    val arguments: Map<String, Any> = emptyMap()
)
