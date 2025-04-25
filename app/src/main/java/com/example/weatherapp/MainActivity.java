package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String url = "https://api.openweathermap.org/data/2.5/weather?q=Nagpur&units=metric&appid=ca6176c23e01d00cb944f5f4ac19f723";

    private ConstraintLayout contentLayout;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        contentLayout = findViewById(R.id.contentLayout);
        icon = findViewById(R.id.icon);

        FrameLayout loadingLayout = findViewById(R.id.loadingLayout);
        ImageView loader = findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        TextView temp = findViewById(R.id.temp);
        TextView cityName = findViewById(R.id.cityName);
        TextView max_temp = findViewById(R.id.max_temp);
        TextView min_temp = findViewById(R.id.min_temp);
        TextView dateView = findViewById(R.id.date);
        TextView humidity = findViewById(R.id.Humidity);
        TextView windSpeed = findViewById(R.id.windSpeed);
        TextView condition = findViewById(R.id.condition);
        TextView sunrise = findViewById(R.id.sunRise);
        TextView sunset = findViewById(R.id.sunset);
        TextView pressure = findViewById(R.id.pressure);
        TextView day = findViewById(R.id.day); // Added declaration

        callAPI(loadingLayout, contentLayout, loader, "Nagpur", temp, cityName, max_temp, min_temp, humidity, windSpeed, sunrise, sunset, condition, pressure, icon);

        SearchView searchView = findViewById(R.id.searchView2);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + s + "&units=metric&appid=ca6176c23e01d00cb944f5f4ac19f723";
                loader.setVisibility(View.VISIBLE);
                runOnUiThread(() -> {
                    loadingLayout.setVisibility(View.VISIBLE);
                    contentLayout.setVisibility(View.GONE);
                    callAPI(loadingLayout, contentLayout, loader, s, temp, cityName, max_temp, min_temp, humidity, windSpeed, sunrise, sunset, condition, pressure, icon);
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        String week = new SimpleDateFormat("EEEE").format(calendar.getTime()); // Use SimpleDateFormat for day of week
        day.setText(week);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        String month = monthFormat.format(calendar.getTime());

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String yearString = yearFormat.format(calendar.getTime());

        dateView.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + month + " " + yearString);
    }

    void callAPI(FrameLayout loadingLayout, ConstraintLayout contentLayout, ImageView loader, String cityName, TextView temp, TextView cityNameView, TextView max_temp, TextView min_temp, TextView humidity, TextView windSpeed, TextView sunrise, TextView sunset, TextView condition, TextView pressure, ImageView icon) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        contentLayout.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        try {
                            Gson gson = new Gson();
                            WeatherData weatherData = gson.fromJson(response.toString(), WeatherData.class);
                            double temperature = weatherData.getMain().getTemp();
                            String description = weatherData.getWeather().get(0).getDescription();
                            String weatherIcon = weatherData.getWeather().get(0).getIcon();
                            temp.setText("" + temperature + "°C");
                            cityNameView.setText(weatherData.getName());
                            max_temp.setText("Max: " + weatherData.getMain().getTemp_max() + " °C");
                            min_temp.setText("Min: " + weatherData.getMain().getTemp_min() + " °C");
                            humidity.setText(weatherData.getMain().getHumidity() + "%");
                            windSpeed.setText(weatherData.getWind().getSpeed() + " m/s");
                            condition.setText(description);

                            long sunriseTime = weatherData.getSys().getSunrise() * 1000;
                            Date sunriseDate = new Date(sunriseTime);
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                            sunrise.setText(sdf.format(sunriseDate));

                            long sunsetTime = weatherData.getSys().getSunset() * 1000;
                            Date sunsetDate = new Date(sunsetTime);
                            sunset.setText(sdf.format(sunsetDate));

                            pressure.setText(weatherData.getMain().getPressure() + " hPa");
                            Picasso.get().load("https://openweathermap.org/img/w/" + weatherIcon + ".png").into(icon);

                            // Change background based on weather condition
                            switch (weatherData.getWeather().get(0).getMain().toLowerCase()) {
                                case "clear":
                                case "haze":
                                    contentLayout.setBackgroundResource(R.drawable.sunny_background);
                                    break;
                                case "clouds":
                                    contentLayout.setBackgroundResource(R.drawable.colud_background);
                                    break;
                                case "rain":
                                case "drizzle":
                                    contentLayout.setBackgroundResource(R.drawable.rain_background);
                                    break;
                                case "snow":
                                    contentLayout.setBackgroundResource(R.drawable.snow_background);
                                    break;
                                default:
                                    contentLayout.setBackgroundResource(R.drawable.default_background);
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent i = new Intent(getApplicationContext(), NotFound.class);
                        startActivity(i);
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
