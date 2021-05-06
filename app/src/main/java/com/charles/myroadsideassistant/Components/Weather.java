package com.charles.myroadsideassistant.Components;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.charles.myroadsideassistant.CustomAlertDialog.WeatherDialog;
import com.charles.myroadsideassistant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;


public class Weather extends AppCompatActivity implements WeatherDialog.ProfileCreateListener {

    TextView cityname;

    private final String url= "https://api.openweathermap.org/data/2.5/weather";
    private final String appid="73277696fe5c27c3ca73b1726ab53762";

    DecimalFormat df= new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityname= (TextView) findViewById(R.id.current_weather_report_id);

        AlertDialog.Builder a1= new AlertDialog.Builder(Weather.this);
        a1.setTitle("Weather Report");
        a1.setMessage("Input city name to view current weather report");
        a1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openDialog();
            }
        });
        a1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog a11= a1.create();
        a11.show();
    }

    private void openDialog() {
        WeatherDialog wd= new WeatherDialog();
        wd.show(getSupportFragmentManager(), "WeatherDialog");
    }

    @Override
    public void applyProfileCreateFields(String cityname) {
        System.out.println("From Weather cityname is: " + cityname);

        final String tempUrl= url + "?q=" + cityname + "&appid=" + appid;

      //  final  String tempUrl= url;

        StringRequest stringRequest= new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output="";
                try {
                    JSONObject jsonResponse= new JSONObject(response);
                    JSONArray jsonArray= jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectweather= jsonArray.getJSONObject(0);
                    String description= jsonObjectweather.getString("description");
                    JSONObject jsonObjectMain= jsonResponse.getJSONObject("main");
                    double temp= jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike= jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure= jsonObjectMain.getInt("pressure");
                    int humidity= jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind= jsonResponse.getJSONObject("wind");
                    String wind= jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds= jsonResponse.getJSONObject("clouds");
                    String clouds= jsonObjectClouds.getString("all");
                    JSONObject jsonObjectsys= jsonResponse.getJSONObject("sys");
                    String countryName= jsonObjectsys.getString("country");
                    String cityName= jsonResponse.getString("name");

                    System.out.println("From applyProfileCreateFields() temp: " + temp + " feelslike: " + feelsLike + " pressure: " + pressure + " humidity: " + humidity);

                    showWeatherData(temp, feelsLike, pressure, humidity, countryName, cityName, description);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Weather.this, "" + error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void showWeatherData(double temp, double feelsLike, float pressure, int humidity, String countryName, String cityName, String description) {
        System.out.println("From showWeatherData temp: " + temp + " feelsLike: " + feelsLike + " pressure: " + pressure + " humidity: " + humidity + " countryName: " + countryName + " description: " + description);
        AlertDialog.Builder a1= new AlertDialog.Builder(Weather.this);
        a1.setTitle("Weather details");
        a1.setIcon(R.drawable.weather1);
        StringBuilder sb1= new StringBuilder();
        sb1.append("Temperature:\t\t\t" + df.format(temp)+" ◦C");
        sb1.append("\n\n");
        sb1.append("Feels Like:\t\t\t" + df.format(feelsLike)+" ◦C");
        sb1.append("\n\n");
        sb1.append("Pressure:\t\t\t" + pressure);
        sb1.append("\n\n");
        sb1.append("Humidity:\t\t\t" + humidity);
        sb1.append("\n\n");
        sb1.append("Country Name:\t\t\t" + countryName);
        sb1.append("\n\n");
        sb1.append("City Name:\t\t\t" + cityName);
        sb1.append("\n\n");
        sb1.append("Description:\t\t\t" + description);
        a1.setMessage(sb1);
        a1.setCancelable(false);
        a1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(Weather.this, MainActivity.class));
                finishAffinity();
            }
        });
        AlertDialog a11= a1.create();
        a11.show();
    }
}