package com.zchat.data.remote.api

import com.zchat.data.remote.dto.AnalyzeRequest
import com.zchat.data.remote.dto.AnalyzeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {

    @POST("ai/analyze/")
    suspend fun analyze(@Body request: AnalyzeRequest): Response<AnalyzeResponse>
}
