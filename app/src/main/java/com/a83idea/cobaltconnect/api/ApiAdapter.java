package com.a83idea.cobaltconnect.api;

import android.content.Context;


import com.a83idea.cobaltconnect.generated.model.Login;
import com.a83idea.cobaltconnect.generated.parser.UserProfileDeserializer;
import com.a83idea.cobaltconnect.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiAdapter {

    public final String TAG = LogUtils.makeLogTag(ApiAdapter.class);

    private ApiAdapter() {
    }


    public static <A> A createRestAdapter(Class<A> AdapterClass, String baseUrl, Context context){


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Login.class,new UserProfileDeserializer());
        Gson gson = gsonBuilder.serializeNulls().create();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(Client.getClientInstance(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return restAdapter.create(AdapterClass);


    }


}
