package com.example.aichatbot1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {

    @Headers("Authorization: Bearer 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\n")
    @POST("completions")
    Call<OpenAIResponse> getResponse(@Body OpenAIRequest request);
}