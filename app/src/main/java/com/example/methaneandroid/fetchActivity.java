package com.example.methaneandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class fetchActivity extends AppCompatActivity {

    String msg = "Android : ";
    public Button submitButt;
    public String token;
    public EditText year;
    public EditText month;
    public EditText day;
    public EditText hour;
    public EditText duration;
    public EditText device_id;

    public EditText keys;
    public EditText filename;

    Retrofit retrofit;

    apiService apiCall;

    public String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        submitButt = findViewById(R.id.submit);
        Bundle extras = getIntent().getExtras();
        token = extras.getString("Token");
        year = findViewById(R.id.yearInput);
        month = findViewById(R.id.monthInput);
        day = findViewById(R.id.dayInput);
        hour = findViewById(R.id.hourInput);
        duration = findViewById(R.id.durInput);
        device_id = findViewById(R.id.devInput);
        keys = findViewById(R.id.keyInput);
        filename = findViewById(R.id.csvInput);
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
        submitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if all editext has been filled in
                //if true call startDownload function
                //else if false warn user
                if(isFieldValid()){
                    startDownload();
                }
                else{
                    Toast.makeText(fetchActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isFieldValid() {
        String yearInput = year.getText().toString().trim();
        String monthInput = month.getText().toString().trim();
        String dayInput = day.getText().toString().trim();
        String hourInput = hour.getText().toString().trim();
        String durInput = duration.getText().toString().trim();
        String devInput = device_id.getText().toString().trim();
        String keysInput = keys.getText().toString().trim();
        String fileInput = filename.getText().toString().trim();
        return !yearInput.isEmpty() && !monthInput.isEmpty()
                && !dayInput.isEmpty() && !hourInput.isEmpty()
                && !durInput.isEmpty() && !devInput.isEmpty()
                && !keysInput.isEmpty() && !fileInput.isEmpty();
    }

    private void startDownload() {
        // Create a DownloadManager request
        Log.d(msg,"download now!");
        int yearInput = Integer.valueOf(year.getText().toString().trim());
        int monthInput = Integer.valueOf(month.getText().toString().trim());
        int dayInput = Integer.valueOf(day.getText().toString().trim());
        int hourInput = Integer.valueOf(hour.getText().toString().trim());
        int durInput = Integer.valueOf(duration.getText().toString().trim());
        String devInput = device_id.getText().toString().trim();
        String keysInput = keys.getText().toString().trim();
        String fileInput = filename.getText().toString().trim();
        Call<ResponseBody> downloadCall = apiCall.download(yearInput, monthInput, dayInput, hourInput,
                                                     durInput, devInput, keysInput, fileInput);
        downloadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(msg, "download now from network!");
                if(response.isSuccessful()){
                    Log.d(msg,"download success");
                    writeResponseBodyToDisk(response.body());
                }
                else{
                    Log.d(msg,"download failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            String fileInput = filename.getText().toString().trim();
            File futureStudioIconFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileInput);
            //or I could save file in self.primary.download? I will try
            Log.d(msg,getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString());
            Log.d(msg,futureStudioIconFile.toString());
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(tag, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                Log.d(tag, "download success now!!!!");
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
