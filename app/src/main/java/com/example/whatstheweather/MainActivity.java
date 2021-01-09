package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
        EditText cityname;   //Declare citynames and result variable
        TextView result1;
    public void findWeather(View view){  // throws UnsupportedEncodingException {  //Onclick function

        Log.i("City", String.valueOf(cityname.getText()));
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //ensures keyboard disappears
        mgr.hideSoftInputFromWindow(cityname.getWindowToken(), 0);
        try {
            String encodedCityName = URLEncoder.encode(String.valueOf(cityname.getText()), "UTF-8");  //encode url
            DownloadTask task = new DownloadTask();  //task object of class DownloadTask
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" +encodedCityName+ "&appid=c2f682c65b95231929e1f5e2a49f22dd");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();//catch exception if url not right
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"Could not find the weather! Please enter a valid city", Toast.LENGTH_LONG);
                }
            });
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityname=(EditText)findViewById(R.id.cityname);  //connect city and result to their views
        result1=(TextView)findViewById(R.id.result);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {  //AsyncTask

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;  //establishing url connection

            try {
                url = new URL(urls[0]); //urls is var args so we select urls[0]

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();   //inputstream channel for url connection

                InputStreamReader reader = new InputStreamReader(in); //establish stream reader to read the url from channel

                int data = reader.read();  //reading the data

                while (data != -1) {

                    char current = (char) data;   //read character by character

                    result += current;  //result,where characters are stored

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Could not find the weather! Please enter a valid city", Toast.LENGTH_LONG);
                    }
                });
            } 

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {  //processing json data
                String message="";
                JSONObject jsonObject = new JSONObject(result);  //creating json object to process the data

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main="";
                    String description="";



                    main=jsonPart.getString("main");
                    description=jsonPart.getString("description");
                    //Log.i("main", jsonPart.getString("main"));
                    //Log.i("description", jsonPart.getString("description"));
                    if(main!="" && description!="")
                    {
                        message+=main+": "+description +"\r\n";
                    }

                }
                if(message!="")
                {
                   result1.setText(message);
                }
                else
                {

                            Toast.makeText(getApplicationContext(),"Could not find the weather! Please enter a valid city", Toast.LENGTH_LONG);

                }


            } catch (JSONException e) {
                //.printStackTrace();



                        Toast.makeText(getApplicationContext(),"Could not find the weather! Please enter a valid city", Toast.LENGTH_LONG);




            }
            }



        }



    }
