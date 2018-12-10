package com.along.longbook.apiservice;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface BaseClient {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.2:8080/longbookapi/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()).build();
}
