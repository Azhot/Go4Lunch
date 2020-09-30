package fr.azhot.go4lunch.service;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static final String TAG = "RetrofitService";
    private static String url = "https://maps.googleapis.com/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        Log.d(TAG, "createService");

        return retrofit.create(serviceClass);
    }
}
