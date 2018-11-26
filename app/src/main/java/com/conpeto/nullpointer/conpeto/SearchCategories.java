package com.conpeto.nullpointer.conpeto;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.conpeto.nullpointer.conpeto.CreateGroup;
import com.conpeto.nullpointer.conpeto.LoginActivity;
import com.conpeto.nullpointer.conpeto.PostLogin;
import com.conpeto.nullpointer.conpeto.R;
import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarException;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.conpeto.nullpointer.conpeto.Service.LocationService;

import java.util.ArrayList;
import java.util.Objects;

public class SearchCategories extends AppCompatActivity {

    private String userID, Category, radius;
    Location loc = LocationService.getLocation();
    private double Lat, Long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_categories);
        userID = getIntent().getStringExtra("user_ID");

        //all xml elements
        final Button search = findViewById(R.id.search);
        final Button goBack = findViewById(R.id.go_Back);

        Spinner distance = findViewById(R.id.spinner2);
        String[] dists = new String[]{"Any", "5 KM", "10 KM", "15 KM", "30 KM"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dists);
        distance.setAdapter(adapter1);

        final Spinner category = findViewById(R.id.spinner1);
        String[] cats = new String[]{"Music and Arts", "Sport", "Food and Conversation", "All"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cats);
        category.setAdapter(adapter2);

        distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        radius = "Any";
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        if (loc == null) {
                            Toast.makeText(SearchCategories.this, "Your location is unspecified!", Toast.LENGTH_LONG).show();
                            radius = "Any";
                        } else
                            radius = "5";
                        break;
                    case 2:
                        // Whatever you want to happen when the first item gets selected
                        if (loc == null) {
                            Toast.makeText(SearchCategories.this, "Your location is unspecified!", Toast.LENGTH_LONG).show();
                            radius = "Any";
                        } else
                            radius = "10";
                        break;

                    case 3:
                        // Whatever you want to happen when the first item gets selected
                        if (loc == null) {
                            Toast.makeText(SearchCategories.this, "Your location is unspecified!", Toast.LENGTH_LONG).show();
                            radius = "Any";
                        } else
                            radius = "15";
                        break;

                    case 4:
                        // Whatever you want to happen when the first item gets selected
                        if (loc == null) {
                            Toast.makeText(SearchCategories.this, "Your location is unspecified!", Toast.LENGTH_LONG).show();
                            radius = "Any";
                        } else
                            radius = "20";
                        break;

                }
            }

            ;

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                radius = "Any";
            }

            ;


        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        Category = "Music and Arts";
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        Category = "Sport";
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        Category = "Food and Conversation";
                        break;

                    case 3:
                        // Whatever you want to happen when the thrid item gets selected
                        Category = "All";
                        break;


                }
            }

            ;

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Category = "Music and Arts";
            }

            ;


        });

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent postlogin = new Intent(SearchCategories.this, PostLogin.class);
                postlogin.putExtra("user_ID", userID);
                SearchCategories.this.startActivity(postlogin);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent next = new Intent(SearchCategories.this, GroupList.class);
                next.putExtra("user_ID", userID);
                next.putExtra("Lat", Lat);
                next.putExtra("Long", Long);
                next.putExtra("radius", radius);
                next.putExtra("cat", Category);
                SearchCategories.this.startActivity(next);
            }
        });

    }
}
