package com.example.togetherremotely;

import android.os.AsyncTask;

public class PingAPI extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        return strings[0];
    }
}
