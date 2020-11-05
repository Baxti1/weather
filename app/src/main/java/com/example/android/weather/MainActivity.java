package com.example.android.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static EditText etCity;
    private static TextView tvWeather;
    private String URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=a5e375fbea59f07ac5e7eec6ec403bb3&lang=ru&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity = findViewById(R.id.etCity);
        tvWeather = findViewById(R.id.tvWeather);

        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }


    public void findOutWeather(View view) {
        DownloadJSONTask task = new DownloadJSONTask();
        String city = etCity.getText().toString().trim();
        task.execute(String.format(URL,city));
    }

    private static class DownloadJSONTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                if (line !=null){
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection !=null){
                    urlConnection.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String name = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String fullWeather = String.format("%s\n\nТемпература: %s\nНа улице: %S",name,temp,description);
                tvWeather.setText(fullWeather);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
