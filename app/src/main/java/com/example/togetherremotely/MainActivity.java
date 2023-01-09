package com.example.togetherremotely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private EditText ipEditText;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.edittext_ip);
    }

    public void addConnection(View view) {
        String ipAddress = ipEditText.getText().toString();
        Request request = new Request.Builder().url("http://" + ipAddress + ":3000/ping").build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
              runOnUiThread(new Runnable() {
                  public void run() {
                      Toast.makeText(getApplicationContext(), "App is not running on " + ipAddress, Toast.LENGTH_LONG).show();
                  }
              });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String responseString = responseBody.string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String hostname = jsonObject.getString("hostname");

                        runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(getApplicationContext(), "Connected to " + hostname, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Homepage.class);

                                intent.putExtra("ipAddress", ipAddress);
                                intent.putExtra("hostname", hostname);

                                startActivity(intent);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}