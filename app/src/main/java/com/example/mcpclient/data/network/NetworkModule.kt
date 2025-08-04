package com.example.mcpclient.data.network

import android.content.Context
import com.example.mcpclient.data.api.McpApiService
import com.example.mcpclient.data.repository.McpRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

object NetworkModule {

    private const val BASE_URL = "https://fi-mcp-dev-k57r.onrender.com/"

    private fun provideGson() = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("User-Agent", "MCP-Android-Client/1.0")
                        .addHeader("Accept", "*/*")
                        .addHeader("Connection", "keep-alive")

                    // Add tunnel-specific headers if needed
                    if (original.url.host.contains("devtunnels.ms")) {
                        requestBuilder.addHeader("X-Forwarded-Proto", "https")
                    }

                    chain.proceed(requestBuilder.build())
                }
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
    }

    fun provideApiService(): McpApiService {
        return provideRetrofit().create(McpApiService::class.java)
    }

    fun provideRepository(context: Context): McpRepository {
        return McpRepository(provideApiService(), context)
    }
}
