package com.along.longbook.hekper;

import com.along.longbook.model.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ErrorUntils {
    public static Response parseError(ResponseBody errorBody, Retrofit retrofit){
        Converter<ResponseBody, Response> converter = retrofit.responseBodyConverter(Response.class, new Annotation[0]);
        try {
            return converter.convert(errorBody);
        } catch (IOException e) {
            return new Response("Unknown");
        }
    }
}
