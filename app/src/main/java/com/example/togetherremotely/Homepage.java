package com.example.togetherremotely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Homepage extends AppCompatActivity {
    private String hostname;
    private String ipAddress;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    private TextView upTimeTextView;
    private TextView tempTextView;

    private ProgressBar ramProgress;
    private TextView ramUsageText;

    private ProgressBar storageProgress;
    private TextView storageUsageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        upTimeTextView = findViewById(R.id.upTimeTextView);
        tempTextView = findViewById(R.id.tempTextView);

        ramProgress = findViewById(R.id.ramProgress);
        ramUsageText = findViewById(R.id.ramUsageText);

        storageProgress = findViewById(R.id.storageProgress);
        storageUsageText = findViewById(R.id.storageUsageText);

        Intent intent = getIntent();
        hostname = intent.getStringExtra("hostname");
        ipAddress = intent.getStringExtra("ipAddress");

        // get basic stats from the api
        getBasicStats();
    }

    private void getBasicStats(){
        Request request = new Request.Builder().url("http://" + ipAddress + ":3000/stats/basic").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "App is not running on " + ipAddress, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String responseString = responseBody.string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);

                        // UP time
                        float upTime = jsonObject.getInt("upTime");
                        upTimeTextView.setText("Up Time: " + upTime + "min");

                        // CPU temperature
                        float temp = jsonObject.getInt("cpuTemperature");
                        tempTextView.setText("CPU temp: " + temp + "C°");

                        // RAM usage
                        JSONObject ram = jsonObject.getJSONObject("ram");
                        double ramUsage =  ((ram.getDouble("used") / ram.getDouble("total")) * 100);
                        ramProgress.setProgress((int) ramUsage);
                        ramUsageText.setText("RAM Usage: " + (int) ramUsage + "%");

                        // Storage usage
                        JSONObject storage = jsonObject.getJSONObject("storage");
                        double storageUsage =  (((storage.getDouble("total") - storage.getDouble("available")) / storage.getDouble("total")) * 100);
                        storageProgress.setProgress((int) storageUsage);
                        storageUsageText.setText("Storage Usage: " + (int) storageUsage + "%");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}