package com.example.methaneandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    String msg = "Android : ";
    String tag = "";
    public Button loginButt;
    public Retrofit retrofit;
    public apiService apiCall;

    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButt = findViewById(R.id.login);
        retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8001")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        // Create an instance of the ApiService
        apiCall = retrofit.create(apiService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(msg,"The onPause() event");
        loginButt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                login();
                Intent intent = new Intent(MainActivity.this, fetchActivity.class);
                intent.putExtra("Token",token);
                Log.d(msg, "checkpoint");
//                System.out.println(token);
                startActivity(intent);
            }

        });
    }
    private void login() {
        //send a login request to backend
        //receive token
        //send token to fetch activity
        Log.d(msg,"log in now!");
        Call<String> loginCall = apiCall.login();
        loginCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(msg,"log cnm");
                if(response.isSuccessful()){
                    token = response.body();
                    Log.d(msg,"log in success");
                }
                else{
                    Log.d(msg,"log in failed");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (t instanceof IOException) {
                    // Network or connectivity error
                    Log.e(tag, "Network error: " + t.getMessage());
                } else {
                    // Other error
                    Log.e(tag, "Unexpected error: " + t.getMessage());
                }
            }
        });
    }
}