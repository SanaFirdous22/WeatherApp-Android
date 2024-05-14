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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    String url = "https://api.openweathermap.org/data/2.5/weather?q=Nagpur&units=metric&appid=ca6176c23e01d00cb944f5f4ac19f723";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout loadingLayout = findViewById(R.id.loadingLayout);
        ConstraintLayout contentLayout = findViewById(R.id.contentLayout);
        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        ImageView icon = findViewById(R.id.icon);
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
        callAPI( loadingLayout , contentLayout , loader ,"Nagpur" ,temp,cityName,max_temp,min_temp,humidity,windSpeed,sunrise,sunset,condition,pressure , icon);
        TextView day = findViewById(R.id.day);
        SearchView searchView = findViewById(R.id.searchView2);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
               // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
             //   cityNameSearch[0] = s;
             url.replace("Nagpur",s);
             loader.setVisibility(View.VISIBLE);
             runOnUiThread(()->{
                 loadingLayout.setVisibility(View.VISIBLE);
                 contentLayout.setVisibility(View.GONE);
                 callAPI( loadingLayout , contentLayout , loader , s ,temp,cityName,max_temp,min_temp,humidity,windSpeed,sunrise,sunset,condition,pressure , icon);
             });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        Date date = new Date();
        String week = "";
        switch (date.getDay()){
            case 1:
                week = "Monday";
                break;
            case 2:
                week = "Tuesday";
                break;
            case 3:
                week = "Wednesday";
                break;
            case 4:
                week = "Thursday";
                break;
            case 5:
                week = "Friday";
                break;
            case 6:
                week = "Saturday";
                break;
            case 7:
                week = "Sunday";
                break;
        }
        //https://openweathermap.org/img/w/${data.weather[0].icon}.png
        day.setText(week);
        Date currentDate = new Date();
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        String month = "";
        switch (date.getMonth()){
            case 0:
                month = "January";
                break;
            case 1:
                month = "February";
                break;
                case 2:
                month = "March";
                    break;
                case 3:
                month = "April";
                    break;
                case 4:
                month = "May";
                    break;
                case 5:
                month = "June";
                    break;
                case 6:
                month = "July";
                    break;
                case 7:
                month = "August";
                    break;
                case 8:
                month = "September";
                    break;
                case 9:
                month = "October";
                break;
            case 10:
                month = "November";
                break;
            case 11:
                month = "December";
        }
        // Use the SimpleDateFormat object to format the date and get the year as a string
        String yearString = dateFormat.format(currentDate);
        dateView.setText(date.getDate()+" "+month+" "+yearString);
    }
    void callAPI(FrameLayout loadingLayout , ConstraintLayout contentLayout , ImageView loader , String name1,TextView temp,TextView cityName , TextView max_temp,TextView min_temp, TextView humidity , TextView windSpeed , TextView sunrise , TextView sunset , TextView condition , TextView pressure , ImageView icon){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.openweathermap.org/data/2.5/weather?q="+name1+"&units=metric&appid=ca6176c23e01d00cb944f5f4ac19f723",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        loadingLayout.setVisibility(View.GONE);
                        contentLayout.setVisibility(View.VISIBLE);
                         loader.setVisibility(View.GONE);
                       // Toast.makeText(getApplicationContext(),"Got data",Toast.LENGTH_SHORT).show();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Log.d("diff",response.toString());
                                Gson gson = new Gson();
                                WeatherData weatherData = gson.fromJson(response.toString(), WeatherData.class);
                                double temperature = weatherData.getMain().getTemp();
                                String description = weatherData.getWeather().get(0).getDescription();
                                //               Toast.makeText(getApplicationContext(),"The temp is:"//+description , Toast.LENGTH_SHORT).show();
                                //tv1.setText("The temp is:"+ String.valueOf(temperature));
                                // weather.setText(description);
                                temp.setText( "" +temperature+"°C");
                                cityName.setText(weatherData.getName());
                                max_temp.setText("Max:"+weatherData.getMain().getTemp_max()+" °C");
                                min_temp.setText(("Min:"+weatherData.getMain().getTemp_min()+" °C"));
                                humidity.setText(""+weatherData.getMain().getHumidity()+"%");
                                windSpeed.setText(""+weatherData.getWind().getSpeed()+ " m/s");
                                condition.setText(description);
                                sunrise.setText(""+weatherData.getSys().getSunrise());
                                sunset.setText(""+weatherData.getSys().getSunset());
                                pressure.setText(""+weatherData.getMain().getPressure()+" hPa");
                               Picasso.get().load("https://openweathermap.org/img/w/"+weatherData.getWeather().get(0).getIcon()+".png").into(icon);
                                //tv2.setText(description);
                            }
                            //    adapter.notifyDataSetChanged(); // Notify adapter of data change
                        } catch (Exception e) {
                            e.printStackTrace();
                            //               Toast.makeText(getApplicationContext(), "Error //parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(getApplicationContext(),"Error while loading data.....Please try different search",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), NotFound.class);
                        startActivity(i);
//                        loader.setVisibility(View.GONE);
                        //               Toast.makeText(getApplicationContext(), "Error f//etching data", Toast.LENGTH_SHORT).show();
                    }
                }
        )

                ;
        requestQueue.add(jsonArrayRequest);
    }
}