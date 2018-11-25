package com.appzone.dolphin.remote;

import com.appzone.dolphin.service.Service;
import com.appzone.dolphin.tags.Tags;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Retrofit retrofit=null;

    private static Retrofit getInstance()
    {
        if (retrofit==null)
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1,TimeUnit.MINUTES)
                    .connectTimeout(1,TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Tags.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

        }
        return retrofit;
    }

    public static Service getService()
    {
        Retrofit retrofit = getInstance();
        Service service =retrofit.create(Service.class);
        return service;
    }
}
