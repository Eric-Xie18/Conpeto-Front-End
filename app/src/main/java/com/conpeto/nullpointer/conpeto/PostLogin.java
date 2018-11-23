package com.conpeto.nullpointer.conpeto;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;

import static com.facebook.login.LoginBehavior.WEB_VIEW_ONLY;

public class PostLogin extends AppCompatActivity  {

private static String radius = "5";
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        final Button bCreateGroup = findViewById(R.id.create_Group);
        final Button bViewGroup = findViewById(R.id.view_groups);
        final Button bJoinGroup = findViewById(R.id.search_Group);
        final TextView radiusDislay = findViewById(R.id.textView2);
        Button bLogOut = findViewById(R.id.log_out_button);


        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
//create a list of items for the spinner.
        String[] items = new String[]{"Last Saved Setting", "5 KM", "10 KM", "15 KM","30 KM","Any"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                                   switch (i) {
                                                       case 0:
                                                           // Whatever you want to happen when the first item gets selected
                                                           radius = radius;
                                                           radiusDislay.setText("The current filter distance is" + radius+ " KM");
                                                           break;
                                                       case 1:
                                                           // Whatever you want to happen when the second item gets selected
                                                           radius = "5";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;
                                                       case 2:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "10";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                       case 3:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "15";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                       case 4:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "30";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                           case 5:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "Any";
                                                               radiusDislay.setText("The geo filter is turned off");
                                                           break;

                                                   }
                                               };

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                          radius = radius;
            };


                                           });

      FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("Firebase instance", "getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();
                System.out.println("Firebase Token is " +token);

            }
        });

        bCreateGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createGroup = new Intent(PostLogin.this,CreateGroup.class);
                createGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(createGroup);
            }
        });

        bJoinGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent searchGroup = new Intent(PostLogin.this,JoinGroup.class);
                searchGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(searchGroup);
            }
        });

        bViewGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(PostLogin.this,ViewGroup.class);
                viewGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(viewGroup);
            }
        });

        bLogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                LoginManager.getInstance().logOut();
                Intent login = new Intent(PostLogin.this,LoginActivity.class);
                PostLogin.this.startActivity(login);
            }
        });


    }
      @Override
     protected void onResume(){
        super.onResume();
        System.out.println("In post login on Resume The radius is" + radius);

     }

    @Override
    public void onBackPressed() {

    }

    public static String gerRadius(){
        return radius;
        }

     public static void modifyRadius(String newRadius){
        radius = newRadius;
        }

}