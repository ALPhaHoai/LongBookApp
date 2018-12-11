package com.along.longbook.apiservice;

import com.along.longbook.model.MultiBookResponse;
import com.along.longbook.model.SingleBookResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookService {

    @GET("book/{book_id}")
    Call<SingleBookResponse> getBook(@Path("book_id") String bookId);

    @GET("book")
    Call<MultiBookResponse> getBooks(@Query("start") int start, @Query("limit") int limit);

    @GET("category/{category_id}/book")
    Call<MultiBookResponse> getBooksHasCate(@Path("category_id") int category, @Query("start") int start, @Query("limit") int limit);

    @GET("book/search")
    Call<MultiBookResponse> searchBooks(@Query("title") String title, @Query("start") int start, @Query("limit") int limit);
}
