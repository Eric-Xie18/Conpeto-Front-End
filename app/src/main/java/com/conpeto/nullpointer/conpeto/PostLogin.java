package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        final Button button = findViewById(R.id.create_Group);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createGroup = new Intent(PostLogin.this,CreateGroup.class);
                createGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(createGroup);
            }
        });


    }



}