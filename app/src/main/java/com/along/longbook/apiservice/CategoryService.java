package com.along.longbook.apiservice;

import com.along.longbook.model.MultiCategoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {

    @GET("category")
    Call<MultiCategoryResponse> getAllCategory();
}
